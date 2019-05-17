package com.example.demo.handler.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;


@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HandlerAEvent extends AbstractEvent<HandlerAEvent> {
    String message;
}
