#!/usr/bin/env python3
"""
Cria 3 gestores (MANAGER) e 12 utilizadores (USER) na API TaskFlow, com a mesma senha
para todos os utilizadores criados por este script. Atribui 4 colaboradores a cada gestor.

Requisitos: API em execução, admin existente (ou bootstrap abaixo).
"""
from __future__ import annotations

import json
import os
import sys
import urllib.error
import urllib.request
from typing import Any


def request_json(
    method: str,
    url: str,
    body: dict[str, Any] | None = None,
    token: str | None = None,
) -> tuple[int, Any]:
    data = None
    headers = {"Accept": "application/json"}
    if body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=120) as resp:
            raw = resp.read().decode("utf-8")
            code = resp.getcode()
            if not raw:
                return code, None
            return code, json.loads(raw)
    except urllib.error.HTTPError as e:
        raw = e.read().decode("utf-8", errors="replace")
        try:
            parsed = json.loads(raw) if raw else None
        except json.JSONDecodeError:
            parsed = raw
        return e.code, parsed


def main() -> None:
    base = os.environ.get("TASKFLOW_API_BASE", "http://localhost:8080/api/v1").rstrip("/")
    admin_email = os.environ.get("ADMIN_EMAIL", "admin@taskflow.com")
    admin_password = os.environ.get("ADMIN_PASSWORD", "Admin@123")
    demo_password = os.environ.get("DEMO_SEED_PASSWORD", "DemoTeam2026")

    bootstrap_url = f"{base}/bootstrap/create-admin"
    code, _ = request_json(
        "POST",
        bootstrap_url,
        {
            "firstName": "Admin",
            "lastName": "Sistema",
            "email": admin_email,
            "password": admin_password,
        },
    )
    if code in (201, 409):
        print(f"Bootstrap create-admin: HTTP {code} (409 = admin já existia)")
    else:
        print(f"Aviso: bootstrap create-admin retornou HTTP {code}", file=sys.stderr)

    login_url = f"{base}/auth/login"
    code, login_body = request_json(
        "POST",
        login_url,
        {"email": admin_email, "password": admin_password},
    )
    if code != 200 or not isinstance(login_body, dict) or "accessToken" not in login_body:
        print(f"Falha no login admin: HTTP {code} body={login_body}", file=sys.stderr)
        sys.exit(1)
    token = login_body["accessToken"]
    print("Login admin OK.")

    def create_user(first: str, last: str, email: str, password: str) -> str:
        url = f"{base}/users"
        code, body = request_json(
            "POST",
            url,
            {
                "firstName": first,
                "lastName": last,
                "email": email,
                "password": password,
            },
            token=token,
        )
        if code == 201 and isinstance(body, dict) and body.get("id"):
            return str(body["id"])
        if code == 409:
            uid = find_user_id_by_email(email)
            if uid:
                print(f"  Utilizador já existia: {email} ({uid})")
                return uid
        print(f"Erro ao criar {email}: HTTP {code} {body}", file=sys.stderr)
        sys.exit(1)

    def find_user_id_by_email(target: str) -> str | None:
        code, body = request_json("GET", f"{base}/users", token=token)
        if code != 200 or not isinstance(body, list):
            return None
        for u in body:
            if isinstance(u, dict) and u.get("email") == target and u.get("id"):
                return str(u["id"])
        return None

    def set_roles(user_id: str, roles: list[str]) -> None:
        code, body = request_json(
            "PUT",
            f"{base}/users/{user_id}/roles",
            {"roles": roles},
            token=token,
        )
        if code != 200:
            print(f"Erro roles {user_id} {roles}: HTTP {code} {body}", file=sys.stderr)
            sys.exit(1)

    def assign_manager(user_id: str, manager_id: str) -> None:
        code, body = request_json(
            "PUT",
            f"{base}/users/{user_id}/manager/{manager_id}",
            token=token,
        )
        if code != 200:
            print(f"Erro assign manager user={user_id} mgr={manager_id}: HTTP {code} {body}", file=sys.stderr)
            sys.exit(1)

    manager_specs = [
        ("Gestor", "Um", "seed-m1@taskflow.demo"),
        ("Gestor", "Dois", "seed-m2@taskflow.demo"),
        ("Gestor", "Três", "seed-m3@taskflow.demo"),
    ]
    manager_ids: list[str] = []
    print(f"Criando gestores (senha demo: {demo_password})...")
    for fn, ln, em in manager_specs:
        uid = create_user(fn, ln, em, demo_password)
        set_roles(uid, ["MANAGER"])
        manager_ids.append(uid)
        print(f"  MANAGER {em} -> {uid}")

    user_emails: list[str] = []
    print("Criando 12 colaboradores USER...")
    for i in range(1, 13):
        em = f"seed-u{i:02d}@taskflow.demo"
        uid = create_user("Colaborador", f"Demo{i:02d}", em, demo_password)
        set_roles(uid, ["USER"])
        user_emails.append(em)
        print(f"  USER {em} -> {uid}")

    print("A atribuir gestores (4 colaboradores por gestor)...")
    for idx, em in enumerate(user_emails):
        uid = find_user_id_by_email(em)
        if not uid:
            print(f"ID em falta para {em}", file=sys.stderr)
            sys.exit(1)
        mgr = manager_ids[idx // 4]
        assign_manager(uid, mgr)
        print(f"  {em} -> gestor {mgr}")

    print("Concluído.")
    print("Resumo:")
    print(f"  API base: {base}")
    print(f"  Senha comum (gestores + colaboradores): {demo_password}")
    print(f"  Admin (login separado): {admin_email}")


if __name__ == "__main__":
    main()
