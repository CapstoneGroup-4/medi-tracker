package edu.capstone4.userserver.services;

import edu.capstone4.userserver.config.IPFSConfig;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class IPFSService {

    private final IPFS ipfs;

    @Autowired
    public IPFSService(IPFSConfig ipfsConfig) {
        // Connect to IPFS local node or specify a remote server (e.g., new IPFS("/dns/ipfs.infura.io/tcp/5001/https"))
//        this.ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        String connectionString = String.format("/ip4/%s/tcp/%s/%s",
                ipfsConfig.getIpfsHost(),
                ipfsConfig.getIpfsPort(), ipfsConfig.getIpfsProtocol());
        this.ipfs = new IPFS(connectionString);
    }

    /**
     * Uploads a file to IPFS and returns the file's IPFS hash.
     *
     * @param file the file to be uploaded
     * @return the IPFS hash of the uploaded file
     * @throws IOException if the file upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        NamedStreamable.InputStreamWrapper fileWrapper = new NamedStreamable.InputStreamWrapper(file.getInputStream());
        MerkleNode response = ipfs.add(fileWrapper).get(0);
        return response.hash.toString();
    }

    /**
     * Retrieves a file from IPFS using its hash.
     *
     * @param hash the IPFS hash of the file
     * @return the file contents as a byte array
     * @throws IOException if the file retrieval fails
     */
    public Optional<byte[]> getFile(String hash) throws IOException {
        try {
            Multihash filePointer = Multihash.fromBase58(hash);
            byte[] fileContents = ipfs.cat(filePointer);
            return Optional.of(fileContents);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
