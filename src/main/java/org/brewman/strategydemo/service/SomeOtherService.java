package org.brewman.strategydemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SomeOtherService {

    public SomeOtherService() {
        log.info("SomeOtherService:");
    }

    public void doesSomething() {
        log.info("doesSomething:");
    }
}
