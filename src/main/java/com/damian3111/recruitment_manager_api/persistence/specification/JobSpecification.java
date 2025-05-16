package com.damian3111.recruitment_manager_api.persistence.specification;

import com.damian3111.recruitment_manager_api.persistence.entities.CandidateEntity;
import com.damian3111.recruitment_manager_api.persistence.entities.JobEntity;
import jakarta.persistence.criteria.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;

@Component
public class JobSpecification implements BaseSpecification<JobEntity, Long>{


}
