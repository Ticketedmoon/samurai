package com.skybreak.samurai.application.persistence.repository;

import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import org.springframework.data.repository.CrudRepository;

public interface GameChangeEventRepository extends CrudRepository<GameChangeEvent, Integer> {

}

