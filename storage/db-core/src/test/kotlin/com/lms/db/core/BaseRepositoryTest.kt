package com.lms.db.core

import com.lms.db.core.config.JpaConfig
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(JpaConfig::class)
abstract class BaseRepositoryTest