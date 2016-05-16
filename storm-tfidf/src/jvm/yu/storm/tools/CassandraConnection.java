package yu.storm.tools;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

import backtype.storm.utils.Utils;

import java.util.Map;

public class CassandraConnection {

	private Cluster cluster;
	private Session session;
	Map<String, String> config;

	public void connect(String node) {
		cluster = Cluster.builder()
				.addContactPoint(node)
				.build();
		Metadata metadata = cluster.getMetadata();
		
		System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
		
		for (Host host: metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		
		session = getSessionWithRetry(cluster, "");
	}
	
	public void close() {
		cluster.close();
	}

	public static Session getSessionWithRetry(Cluster cluster, String keyspace) {
        while (true) {
            try {
            	if (keyspace != "") {
            		return cluster.connect(keyspace);
            	}
            	else {
            		return cluster.connect();
            	}
            } catch (NoHostAvailableException e) {
                System.out.printf("All Cassandra Hosts offline. Waiting to try again.");
                Utils.sleep(1000);
            }
        }
    }
}