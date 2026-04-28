#!/usr/bin/env python3
"""
Smoke API regressivo (23 cenarios) — alinhado a specs/endpoint-test-guide.md

Uso:
  python3 scripts/smoke_api.py
  BASE_URL=http://localhost:8082/api/v1 python3 scripts/smoke_api.py

Requisitos: API no ar; seed admin (admin@taskflow.com / Admin@123).
"""
from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.request
from datetime import date, timedelta


def main() -> int:
    base = os.environ.get("BASE_URL", "http://localhost:8082/api/v1").rstrip("/")

    def request(method: str, path: str, token: str | None = None, body: dict | None = None):
        headers = {"Content-Type": "application/json"}
        if token:
            headers["Authorization"] = f"Bearer {token}"
        data = None if body is None else json.dumps(body).encode("utf-8")
        req = urllib.request.Request(base + path, data=data, headers=headers, method=method)
        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                raw = resp.read().decode("utf-8")
                return resp.getcode(), (json.loads(raw) if raw else {})
        except urllib.error.HTTPError as e:
            raw = e.read().decode("utf-8")
            try:
                payload = json.loads(raw) if raw else {}
            except json.JSONDecodeError:
                payload = {"raw": raw}
            return e.code, payload

    def run(name: str, expected: int, method: str, path: str, token: str | None = None, body: dict | None = None):
        code, payload = request(method, path, token=token, body=body)
        ok = code == expected
        status = "PASS" if ok else "FAIL"
        print(f"{status} | {name} | expected={expected} got={code}")
        if not ok:
            print("  detail:", str(payload.get("detail", payload))[:300])
        return ok, code, payload

    results: list[bool] = []

    ok, _, admin_login = run("Seed admin login", 200, "POST", "/auth/login", body={
        "email": "admin@taskflow.com",
        "password": "Admin@123",
    })
    results.append(ok)
    admin_token = admin_login.get("accessToken")
    if not admin_token:
        print("FATAL: admin accessToken ausente")
        return 1

    sfx = str(int(time.time()))[-6:]
    pwd = "SecurePass@123"

    def em(prefix: str) -> str:
        return f"{prefix}_{sfx}@company.com"

    # Janelas unicas por execucao (evita 409 por PENDING/APPROVED de outros utilizadores no BD)
    seed = int(sfx) % 340 + 40
    base_d = date(2031, 1, 1) + timedelta(days=seed)
    u1_start, u1_end = base_d, base_d + timedelta(days=5)
    ov_start = base_d + timedelta(days=60)
    ov_end = ov_start + timedelta(days=5)
    # Sobreposicao com ferias aprovadas de user1 (user2) -> 409 por overlap PENDING/APPROVED
    u2_start, u2_end = base_d + timedelta(days=2), base_d + timedelta(days=8)

    ok, _, manager_user = run("Create manager user as ADMIN", 201, "POST", "/users", token=admin_token, body={
        "email": em("manager"), "password": pwd, "firstName": "Manager", "lastName": "One",
    })
    results.append(ok)
    ok, _, manager2_user = run("Create second manager (outsider reports here)", 201, "POST", "/users", token=admin_token, body={
        "email": em("manager2"), "password": pwd, "firstName": "Manager", "lastName": "Two",
    })
    results.append(ok)
    ok, _, user1 = run("Create collaborator user as ADMIN", 201, "POST", "/users", token=admin_token, body={
        "email": em("user1"), "password": pwd, "firstName": "User", "lastName": "One",
    })
    results.append(ok)
    ok, _, user2 = run("Create second collaborator", 201, "POST", "/users", token=admin_token, body={
        "email": em("user2"), "password": pwd, "firstName": "User", "lastName": "Two",
    })
    results.append(ok)
    ok, _, outsider = run("Create outsider collaborator", 201, "POST", "/users", token=admin_token, body={
        "email": em("outsider"), "password": pwd, "firstName": "Out", "lastName": "Sider",
    })
    results.append(ok)

    manager_id = manager_user["id"]
    manager2_id = manager2_user["id"]
    user1_id = user1["id"]
    user2_id = user2["id"]
    outsider_id = outsider["id"]

    ok, _, _ = run("Assign MANAGER role", 200, "PUT", f"/users/{manager_id}/roles", token=admin_token, body={
        "roleName": "MANAGER",
    })
    results.append(ok)
    ok, _, _ = run("Assign MANAGER role to second manager", 200, "PUT", f"/users/{manager2_id}/roles", token=admin_token, body={
        "roleName": "MANAGER",
    })
    results.append(ok)

    ok, _, _ = run("Assign manager to collaborator", 200, "PUT", f"/users/{user1_id}/manager/{manager_id}", token=admin_token)
    results.append(ok)
    ok, _, _ = run("Assign manager to second collaborator", 200, "PUT", f"/users/{user2_id}/manager/{manager_id}", token=admin_token)
    results.append(ok)
    ok, _, _ = run("Assign outsider to second manager (not Manager One team)", 200, "PUT", f"/users/{outsider_id}/manager/{manager2_id}", token=admin_token)
    results.append(ok)

    ok, _, manager_login = run("Login manager", 200, "POST", "/auth/login", body={"email": em("manager"), "password": pwd})
    results.append(ok)
    manager_token = manager_login["accessToken"]

    ok, _, user1_login = run("Login user", 200, "POST", "/auth/login", body={"email": em("user1"), "password": pwd})
    results.append(ok)
    user1_token = user1_login["accessToken"]

    ok, _, user2_login = run("Login user2", 200, "POST", "/auth/login", body={"email": em("user2"), "password": pwd})
    results.append(ok)
    user2_token = user2_login["accessToken"]

    ok, _, outsider_login = run("Login outsider", 200, "POST", "/auth/login", body={"email": em("outsider"), "password": pwd})
    results.append(ok)
    outsider_token = outsider_login["accessToken"]

    mgr_trip_start = base_d + timedelta(days=120)
    ok, _, _ = run("MANAGER cannot create vacation", 403, "POST", "/vacation-requests", token=manager_token, body={
        "startDate": mgr_trip_start.isoformat(),
        "endDate": (mgr_trip_start + timedelta(days=4)).isoformat(),
        "reason": "Manager trip blocked by business rule",
    })
    results.append(ok)

    ok, _, vac1 = run("USER creates vacation", 201, "POST", "/vacation-requests", token=user1_token, body={
        "startDate": u1_start.isoformat(),
        "endDate": u1_end.isoformat(),
        "reason": "Family trip vacation period",
    })
    results.append(ok)
    vac1_id = vac1["id"]

    ok, _, _ = run("MANAGER approves team vacation", 200, "PUT", f"/vacation-requests/{vac1_id}/approve", token=manager_token)
    results.append(ok)

    ok, _, _ = run("Overlap with approved vacation denied", 409, "POST", "/vacation-requests", token=user2_token, body={
        "startDate": u2_start.isoformat(),
        "endDate": u2_end.isoformat(),
        "reason": "Overlap period test case here",
    })
    results.append(ok)

    ok, _, outsider_vac = run("Outsider user creates vacation", 201, "POST", "/vacation-requests", token=outsider_token, body={
        "startDate": ov_start.isoformat(),
        "endDate": ov_end.isoformat(),
        "reason": "Solo vacation trip planned abroad",
    })
    results.append(ok)
    outsider_vac_id = outsider_vac["id"]

    ok, _, _ = run("Manager cannot approve non-team vacation", 403, "PUT", f"/vacation-requests/{outsider_vac_id}/approve", token=manager_token)
    results.append(ok)

    ok, _, _ = run("Cannot delete user with vacations", 400, "DELETE", f"/users/{user1_id}", token=admin_token)
    results.append(ok)

    ok, _, _ = run("Cannot edit processed vacation", 409, "PUT", f"/vacation-requests/{vac1_id}", token=user1_token, body={
        "startDate": (u1_start + timedelta(days=1)).isoformat(),
        "endDate": (u1_end + timedelta(days=1)).isoformat(),
        "reason": "Update after approval attempt blocked",
    })
    results.append(ok)

    passed = sum(results)
    total = len(results)
    print(f"TOTAL: {total} | PASS: {passed} | FAIL: {total - passed}")
    return 0 if passed == total else 2


if __name__ == "__main__":
    sys.exit(main())
