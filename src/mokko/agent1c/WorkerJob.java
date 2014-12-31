/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.agent1c;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.UUID;

/**
 *
 * @author psyriccio
 */
@XStreamAlias("job")
public class WorkerJob {

    @XStreamAlias("uid")
    @XStreamAsAttribute
    private UUID uid;
    
    @XStreamAlias("queueName")
    @XStreamAsAttribute
    private String queueName;
    
    @XStreamAlias("owner")
    @XStreamAsAttribute
    private String owner;
    
    @XStreamAlias("code")
    private String code;

    @XStreamAlias("result")
    private String result;

    @XStreamAlias("tag")
    @XStreamAsAttribute
    private String tag;
    
    public WorkerJob() {
        uid = UUID.randomUUID();
    }

    public WorkerJob(UUID uid, String queueName, String owner, String code, String result, String tag) {
        this.uid = uid;
        this.queueName = queueName;
        this.owner = owner;
        this.code = code;
        this.result = result;
        this.tag = tag;
    }

    public WorkerJob(String queueName, String owner, String code, String result, String tag) {
        this.uid = UUID.randomUUID();
        this.queueName = queueName;
        this.owner = owner;
        this.code = code;
        this.result = result;
        this.tag = tag;
    }

    public WorkerJob(String uid, String queueName, String owner, String code, String result, String tag) {
        this.uid = UUID.fromString(uid);
        this.queueName = queueName;
        this.owner = owner;
        this.code = code;
        this.result = result;
        this.tag = tag;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
}
