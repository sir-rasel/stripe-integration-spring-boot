package org.sir.stripeintegration.core.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sir.stripeintegration.core.shared.EntityAuditFields;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@EntityScan
public class PaymentMethodEntity extends EntityAuditFields implements Persistable<String> {
    @Id
    public String id;

    @NotNull
    public String customerId;

    @Transient
    private boolean isNewEntry;

    @Override
    public boolean isNew() {
        return isNewEntry;
    }
}
