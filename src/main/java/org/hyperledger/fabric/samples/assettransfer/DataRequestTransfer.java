/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Contract(
        name = "dataRequest",
        info = @Info(
                title = "Data Request Transfer",
                description = "Chaincode to manage Data Requests",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "data.request@example.com",
                        name = "Data Request Manager",
                        url = "https://hyperledger.example.com")))
@Default
public final class DataRequestTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum DataRequestErrors {
        DATA_REQUEST_NOT_FOUND,
        DATA_REQUEST_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitDataRequestLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateDataRequest(ctx, "request1", "Request 1 Description", "Alice", ctx.getStub().getTxTimestamp().getEpochSecond(), "Admin", null, false);
        CreateDataRequest(ctx, "request2", "Request 2 Description", "Bob", ctx.getStub().getTxTimestamp().getEpochSecond(), "Admin", null, false);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest CreateDataRequest(final Context ctx, final String requestId, final String description,
                                         final String owner, final long createdOn, final String createdBy,
                                         final Map<String, DataRequest.Status> approvers, final Boolean deleted) {
        ChaincodeStub stub = ctx.getStub();

        if (DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("DataRequest %s already exists", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataRequestErrors.DATA_REQUEST_ALREADY_EXISTS.toString());
        }

        DataRequest dataRequest = new DataRequest(requestId, description, owner, createdOn, createdBy, approvers, deleted);
        String dataRequestJSON = genson.serialize(dataRequest);
        stub.putStringState(requestId, dataRequestJSON);

        return dataRequest;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public DataRequest ReadDataRequest(final Context ctx, final String requestId) {
        ChaincodeStub stub = ctx.getStub();
        String dataRequestJSON = stub.getStringState(requestId);

        if (dataRequestJSON == null || dataRequestJSON.isEmpty()) {
            String errorMessage = String.format("DataRequest %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataRequestErrors.DATA_REQUEST_NOT_FOUND.toString());
        }

        return genson.deserialize(dataRequestJSON, DataRequest.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest UpdateDataRequest(final Context ctx, final String requestId, final String description,
                                         final String owner, final long createdOn, final String createdBy,
                                         final Map<String, DataRequest.Status> approvers, final Boolean deleted) {
        ChaincodeStub stub = ctx.getStub();

        if (!DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("DataRequest %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataRequestErrors.DATA_REQUEST_NOT_FOUND.toString());
        }

        DataRequest updatedDataRequest = new DataRequest(requestId, description, owner, createdOn, createdBy, approvers, deleted);
        String updatedDataRequestJSON = genson.serialize(updatedDataRequest);
        stub.putStringState(requestId, updatedDataRequestJSON);

        return updatedDataRequest;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteDataRequest(final Context ctx, final String requestId) {
        ChaincodeStub stub = ctx.getStub();

        if (!DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("DataRequest %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataRequestErrors.DATA_REQUEST_NOT_FOUND.toString());
        }

        stub.delState(requestId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean DataRequestExists(final Context ctx, final String requestId) {
        ChaincodeStub stub = ctx.getStub();
        String dataRequestJSON = stub.getStringState(requestId);

        return (dataRequestJSON != null && !dataRequestJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest TransferDataRequestOwnership(final Context ctx, final String requestId, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String dataRequestJSON = stub.getStringState(requestId);

        if (dataRequestJSON == null || dataRequestJSON.isEmpty()) {
            String errorMessage = String.format("DataRequest %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataRequestErrors.DATA_REQUEST_NOT_FOUND.toString());
        }

        DataRequest dataRequest = genson.deserialize(dataRequestJSON, DataRequest.class);

        DataRequest updatedDataRequest = new DataRequest(dataRequest.getRequestId(), dataRequest.getDescription(),
                newOwner, dataRequest.getCreatedOn(), dataRequest.getCreatedBy(), dataRequest.getApprovers(),
                dataRequest.getDeleted());
        String updatedDataRequestJSON = genson.serialize(updatedDataRequest);
        stub.putStringState(requestId, updatedDataRequestJSON);

        return updatedDataRequest;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllDataRequests(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<DataRequest> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            DataRequest dataRequest = genson.deserialize(result.getStringValue(), DataRequest.class);
            queryResults.add(dataRequest);
            System.out.println(dataRequest.toString());
        }

        return genson.serialize(queryResults);
    }
}
