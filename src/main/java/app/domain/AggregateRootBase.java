package app.domain;

import java.util.UUID;

/**
 * Created by marc on 15/01/16.
 */
public abstract class AggregateRootBase {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
