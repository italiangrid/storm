package it.grid.storm.authz.sa.model;

public class SpacePermission {

  /**
    RELEASE_SPACE (D)
    UPDATE_SPACE (U)
    READ_FROM_SPACE (R)
    WRITE_TO_SPACE (W)
    STAGE_TO_SPACE (S)
    REPLICATE_FROM_SPACE(C)
    PURGE_FROM_SPACE (P)
    QUERY_SPACE (Q)
    MODIFY_SPACE_ACL (M)
  **/


 public final static SpacePermission RELEASE_SPACE = new SpacePermission(
         "RELEASE_SPACE", 'D');
 public final static SpacePermission UPDATE_SPACE = new SpacePermission(
         "UPDATE_SPACE", 'U');
 public final static SpacePermission READ_FROM_SPACE = new SpacePermission(
         "READ_FROM_SPACE", 'R');
 public final static SpacePermission WRITE_TO_SPACE = new SpacePermission(
         "WRITE_TO_SPACE", 'W');
 public final static SpacePermission STAGE_TO_SPACE = new SpacePermission(
         "STAGE_TO_SPACE", 'S');
 public final static SpacePermission REPLICATE_FROM_SPACE = new SpacePermission(
         "REPLICATE_FROM_SPACE", 'C');
 public final static SpacePermission PURGE_FROM_SPACE = new SpacePermission(
         "PURGE_FROM_SPACE", 'P');
 public final static SpacePermission QUERY_SPACE = new SpacePermission(
         "QUERY_SPACE", 'Q');
 public final static SpacePermission MODIFY_SPACE_ACL = new SpacePermission(
         "MODIFY_SPACE_ACL", 'M');
 public final static SpacePermission UNDEFINED = new SpacePermission(
         "UNDEF", 'X');


  private String description;
  private char operation;

  private SpacePermission(String description, char operation) {
       this.description = description;
       this.operation = operation;
    }

  public static SpacePermission getSpaceOperation(String op) {
     if (op.equals("D")) return RELEASE_SPACE;
     if (op.equals("U")) return UPDATE_SPACE;
     if (op.equals("R")) return READ_FROM_SPACE;
     if (op.equals("W")) return WRITE_TO_SPACE;
     if (op.equals("S")) return STAGE_TO_SPACE;
     if (op.equals("C")) return REPLICATE_FROM_SPACE;
     if (op.equals("P")) return PURGE_FROM_SPACE;
     if (op.equals("Q")) return QUERY_SPACE;
     if (op.equals("M")) return MODIFY_SPACE_ACL;
     return UNDEFINED;
  }

  public String toString() {
      return String.valueOf(this.operation);
  }

}
