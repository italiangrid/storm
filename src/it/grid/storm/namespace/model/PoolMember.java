package it.grid.storm.namespace.model;

public class PoolMember {

  private int memberID;
  private int memberWeight;
  private Protocol memberProtocol;

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

  public void setMemberProtocol(Protocol protocol) {
    this.memberProtocol = protocol;
  }

  public Protocol getMemeberProtocol() {
    return this.memberProtocol;
  }

}
