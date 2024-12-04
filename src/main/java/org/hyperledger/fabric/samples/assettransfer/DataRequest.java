/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DataType()
public final class DataRequest {

    @Property()
    private final String requestId;

    @Property()
    private final String description;

    @Property()
    private final String owner;

    @Property()
    private final Long createdOn;

    @Property()
    private final String createdBy;

    @Property()
    private final Map<String, Status> approvers;

    @Property()
    private final Boolean deleted;

    public enum Status {
        approved, rejected, pending, deemedApproved, deemedRejected
    }

    public DataRequest(@JsonProperty("requestId") final String requestId,
                       @JsonProperty("description") final String description,
                       @JsonProperty("owner") final String owner,
                       @JsonProperty("createdOn") final long createdOn,
                       @JsonProperty("createdBy") final String createdBy,
                       @JsonProperty("approvers") final Map<String, Status> approvers,
                       @JsonProperty("deleted") final Boolean deleted) {
        this.requestId = requestId;
        this.description = description;
        this.owner = owner;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.approvers = approvers == null ? new HashMap<>() : approvers;
        this.deleted = deleted;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Map<String, Status> getApprovers() {
        return approvers;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        DataRequest other = (DataRequest) obj;

        return Objects.deepEquals(
                new Object[] {getRequestId(), getDescription(), getOwner(), getCreatedOn(), getCreatedBy(), getDeleted()},
                new Object[] {other.getRequestId(), other.getDescription(), other.getOwner(), other.getCreatedOn(), other.getCreatedBy(), other.getDeleted()})
                &&
                Objects.deepEquals(getApprovers(), other.getApprovers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestId(), getDescription(), getOwner(), getCreatedOn(), getCreatedBy(), getApprovers(), getDeleted());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " [requestId=" + requestId
                + ", description=" + description
                + ", owner=" + owner
                + ", createdOn=" + createdOn
                + ", createdBy=" + createdBy
                + ", approvers=" + approvers
                + ", deleted=" + deleted
                + "]";
    }
}
