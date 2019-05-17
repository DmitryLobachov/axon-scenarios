package com.example.demo.handler.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;


@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbstractEvent<E extends AbstractEvent<E>> {
    Long workflowId;

    public E setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
        return (E) this;
    }
}
