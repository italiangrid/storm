package it.grid.storm.authz.sa.model;

public class SpaceAccessMask {

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


 public final static SpaceAccessMask RELEASE_SPACE = new SpaceAccessMask(
         "RELEASE_SPACE", 'D');
 public final static SpaceAccessMask UPDATE_SPACE = new SpaceAccessMask(
         "UPDATE_SPACE", 'U');
 public final static SpaceAccessMask READ_FROM_SPACE = new SpaceAccessMask(
         "READ_FROM_SPACE", 'R');
 public final static SpaceAccessMask WRITE_TO_SPACE = new SpaceAccessMask(
         "WRITE_TO_SPACE", 'W');
 public final static SpaceAccessMask STAGE_TO_SPACE = new SpaceAccessMask(
         "STAGE_TO_SPACE", 'S');
 public final static SpaceAccessMask REPLICATE_FROM_SPACE = new SpaceAccessMask(
         "REPLICATE_FROM_SPACE", 'C');
 public final static SpaceAccessMask PURGE_FROM_SPACE = new SpaceAccessMask(
         "PURGE_FROM_SPACE", 'P');
 public final static SpaceAccessMask QUERY_SPACE = new SpaceAccessMask(
         "QUERY_SPACE", 'Q');
 public final static SpaceAccessMask MODIFY_SPACE_ACL = new SpaceAccessMask(
         "MODIFY_SPACE_ACL", 'M');
 public final static SpaceAccessMask UNDEFINED = new SpaceAccessMask(
         "UNDEF", 'X');


  private final String description;
  private final char operation;

  private SpaceAccessMask(String description, char operation) {
       this.description = description;
       this.operation = operation;
    }

  public static SpaceAccessMask getSpaceOperation(String op) {
     if (op.equals("D")) {
        return RELEASE_SPACE;
    }
     if (op.equals("U")) {
        return UPDATE_SPACE;
    }
     if (op.equals("R")) {
        return READ_FROM_SPACE;
    }
     if (op.equals("W")) {
        return WRITE_TO_SPACE;
    }
     if (op.equals("S")) {
        return STAGE_TO_SPACE;
    }
     if (op.equals("C")) {
        return REPLICATE_FROM_SPACE;
    }
     if (op.equals("P")) {
        return PURGE_FROM_SPACE;
    }
     if (op.equals("Q")) {
        return QUERY_SPACE;
    }
     if (op.equals("M")) {
        return MODIFY_SPACE_ACL;
    }
     return UNDEFINED;
  }

  @Override
public String toString() {
      return String.valueOf(this.operation);
  }

}
