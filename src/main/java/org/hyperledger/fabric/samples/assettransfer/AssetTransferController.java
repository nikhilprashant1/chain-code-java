package org.hyperledger.fabric.samples.assettransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetTransferController {

    @Autowired
    private AssetTransferService assetTransferService;

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetTransferService.createAsset(asset);
    }

    @GetMapping("/{assetID}")
    public Asset getAsset(@PathVariable String assetID) {
        return assetTransferService.getAsset(assetID);
    }

    @PutMapping("/{assetID}")
    public Asset updateAsset(@PathVariable String assetID, @RequestBody Asset asset) {
        return assetTransferService.updateAsset(assetID, asset);
    }

    @DeleteMapping("/{assetID}")
    public void deleteAsset(@PathVariable String assetID) {
        assetTransferService.deleteAsset(assetID);
    }

    @GetMapping("/all")
    public List<Asset> getAllAssets() {
        return assetTransferService.getAllAssets();
    }
}

