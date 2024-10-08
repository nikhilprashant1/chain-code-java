package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class AssetTransferService {

    private Gateway gateway;

    public AssetTransferService() {
        // Initialize the gateway (load credentials, create a network, etc.)
        // Ensure to have your credentials and config set up correctly
        Path walletPath = Paths.get("path/to/your/wallet");
        Path networkConfigPath = Paths.get("path/to/your/network-config.yaml");

        // Initialize the Fabric Gateway
        try {
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);
            Gateway.Builder builder = Gateway.createBuilder();
            gateway = builder.identity(wallet, "your_identity").networkConfig(networkConfigPath).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Asset createAsset(Asset asset) {
        try {
            Network network = gateway.getNetwork("your_channel_name");
            Contract contract = network.getContract("basic");
            // Invoke the CreateAsset transaction
            contract.submitTransaction("CreateAsset", asset.getAssetID(), asset.getColor(), String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));
            return asset; // Return the created asset
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Asset getAsset(String assetID) {
        try {
            Network network = gateway.getNetwork("your_channel_name");
            Contract contract = network.getContract("basic");
            byte[] result = contract.evaluateTransaction("ReadAsset", assetID);
            return new Genson().deserialize(new String(result), Asset.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Asset updateAsset(String assetID, Asset asset) {
        try {
            Network network = gateway.getNetwork("your_channel_name");
            Contract contract = network.getContract("basic");
            contract.submitTransaction("UpdateAsset", assetID, asset.getColor(), String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));
            return asset; // Return the updated asset
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAsset(String assetID) {
        try {
            Network network = gateway.getNetwork("your_channel_name");
            Contract contract = network.getContract("basic");
            contract.submitTransaction("DeleteAsset", assetID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Asset> getAllAssets() {
        try {
            Network network = gateway.getNetwork("your_channel_name");
            Contract contract = network.getContract("basic");
            byte[] result = contract.evaluateTransaction("GetAllAssets");
            return new Genson().deserialize(new String(result), List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}