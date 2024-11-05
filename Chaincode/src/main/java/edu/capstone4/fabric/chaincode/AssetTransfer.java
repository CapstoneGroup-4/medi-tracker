/*
 * SPDX-License-Identifier: Apache-2.0
 * Author: Hong
 * Update Time: 2024.11.3
 * Version: 0
 */

package edu.capstone4.fabric.chaincode;

import java.util.ArrayList;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

@Contract(
        name = "medi-tracker",
        info = @Info(
                title = "Record Management",
                description = "Record CRUD",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "h.chang@student.fdu.edu",
                        name = "Hong Chang",
                        url = "https://github.com/orgs/CapstoneGroup-4/repositories")))
@Default
public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Initializes the ledger with some sample assets for testing.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        CreateAsset(ctx, "record1", "patient1", "hospital1", "doctor1", "http:URL1", "hashcode1");
        CreateAsset(ctx, "record2", "patient2", "hospital2", "doctor2", "http:URL2", "hashcode2");
        CreateAsset(ctx, "record3", "patient3", "hospital3", "doctor3", "http:URL3", "hashcode3");
    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset CreateAsset(final Context ctx, final String recordID, final String patientID,
                             final String hospitalID, final String doctorID,
                             final String recordURL, final String recordHashCode) {
        ChaincodeStub stub = ctx.getStub();

        if (AssetExists(ctx, recordID)) {
            String errorMessage = String.format("Asset %s already exists", recordID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        final int version = 0;
        final ZonedDateTime updateTime = ZonedDateTime.now(ZoneId.of("GMT"));

        final Asset asset = new Asset(recordID, patientID, hospitalID, doctorID, recordURL, recordHashCode, version, updateTime);
        final String sortedJson = genson.serialize(asset);

        stub.putStringState(recordID, sortedJson);
        return asset;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @return the asset found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadAsset(final Context ctx, final String recordID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(recordID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset with recordID %s does not exist", recordID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        return genson.deserialize(assetJSON, Asset.class);
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @return the updated asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateAsset(final Context ctx, final String recordID, final String patientID, final String hospitalID,
                             final String doctorID, final String recordURL, final String recordHashCode) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(recordID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset with recordID %s does not exist", recordID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        final int updateVersion = asset.getVersion() + 1;
        final ZonedDateTime updateTime = ZonedDateTime.now(ZoneId.of("GMT"));

        Asset updatedAsset = new Asset(recordID, patientID, hospitalID, doctorID, recordURL, recordHashCode, updateVersion, updateTime);
        stub.putStringState(recordID, genson.serialize(updatedAsset));

        final String oldVersionRecordID = asset.getRecordID() + "_Version" + asset.getVersion();
        Asset oldAsset = new Asset(oldVersionRecordID, asset.getPatientID(), asset.getHospitalID(),
                asset.getDoctorID(), asset.getRecordURL(), asset.getRecordHashCode(),
                asset.getVersion(), asset.getUpdateTime());
        stub.putStringState(oldVersionRecordID, genson.serialize(oldAsset));

        return updatedAsset;
    }

    /**
     * Deletes an asset from the ledger.
     *
     * @param ctx the transaction context
     * @param recordID the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String recordID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(recordID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", recordID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        final String deletedRecordID = asset.getRecordID() + "_Deleted";

        Asset deletedAsset = new Asset(deletedRecordID, asset.getPatientID(), asset.getHospitalID(),
                asset.getDoctorID(), asset.getRecordURL(), asset.getRecordHashCode(),
                asset.getVersion(), asset.getUpdateTime());
        stub.putStringState(deletedRecordID, genson.serialize(deletedAsset));
        stub.delState(recordID);
    }

    /**
     * Checks the existence of the asset on the ledger.
     *
     * @param ctx the transaction context
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String recordID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJson = stub.getStringState(recordID);
        return (assetJson != null && !assetJson.isEmpty());
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return JSON string of all assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Asset> queryResults = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            queryResults.add(genson.deserialize(result.getStringValue(), Asset.class));
        }

        return genson.serialize(queryResults);
    }
}
