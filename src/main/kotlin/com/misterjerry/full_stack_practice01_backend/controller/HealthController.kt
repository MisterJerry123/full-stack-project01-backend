package com.misterjerry.full_stack_practice01_backend.controller

import org.springframework.web.bind.annotation.GetMapping

class HealthController {

    @GetMapping("/health")
    fun health():String{
        return "ok"
    }
}