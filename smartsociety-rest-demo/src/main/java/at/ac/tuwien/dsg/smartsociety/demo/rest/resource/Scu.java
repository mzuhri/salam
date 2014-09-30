package at.ac.tuwien.dsg.smartsociety.demo.rest.resource;

import java.util.ArrayList;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SCU", description = "SCU representation")
public class Scu {
    @ApiModelProperty(value = "SCU's id", required = true) private Integer id;
	@ApiModelProperty(value = "SCU's name", required = true) private String name;
    @ApiModelProperty(value = "SCU's members", required = true) private ArrayList<Peer> peers;
		
	public Scu() {
	}

    public Scu(Integer id, String name, ArrayList<Peer> peers) {
        super();
        this.id = id;
        this.name = name;
        this.peers = peers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Peer> getPeers() {
        return peers;
    }

    public void setPeers(ArrayList<Peer> peers) {
        this.peers = peers;
    }

}
