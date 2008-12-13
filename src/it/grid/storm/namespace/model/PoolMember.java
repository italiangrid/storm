package it.grid.storm.namespace.model;

public class PoolMember {

  private int memberID;
  private int memberWeight;
  private TransportProtocol memberProtocol;

  public PoolMember(int memberID, int weight) {
    this.memberID = memberID;
    this.memberWeight = weight;
  }

  public int getMemberID() {
    return this.memberID;
  }

  public int getMemberWeight() {
    return this.memberWeight;
  }

  public void setMemberProtocol(TransportProtocol protocol) {
    this.memberProtocol = protocol;
  }

  public TransportProtocol getMemeberProtocol() {
    return this.memberProtocol;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(memberProtocol + " --> Weight: "+this.memberWeight );
    return sb.toString();
  }

}
