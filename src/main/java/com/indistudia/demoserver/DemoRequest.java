package com.indistudia.demoserver;

public record DemoRequest(
        String accountNumber,
        Integer cvv
) {
}
