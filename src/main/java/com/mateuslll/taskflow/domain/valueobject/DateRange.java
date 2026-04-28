package com.mateuslll.taskflow.domain.valueobject;

import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateRange(LocalDate startDate, LocalDate endDate) {

    public DateRange {
        if (startDate == null) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Data de início"));
        }
        
        if (endDate == null) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Data de fim"));
        }
        
        if (endDate.isBefore(startDate)) {
            throw new DomainException(ResourceMessages.START_DATE_AFTER_END_DATE.getMessage());
        }
    }

public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

public boolean overlaps(DateRange other) {
        if (other == null) {
            return false;
        }
        
        return !(this.endDate.isBefore(other.startDate) || 
                 this.startDate.isAfter(other.endDate));
    }

public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

public boolean isInFuture() {
        return startDate.isAfter(LocalDate.now());
    }

public boolean isPast() {
        return endDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("%s até %s (%d dias)", 
                startDate, endDate, getDays());
    }
}
