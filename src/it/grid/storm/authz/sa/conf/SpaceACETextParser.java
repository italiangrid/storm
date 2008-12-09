package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.sa.model.SpaceACE;
import it.grid.storm.authz.sa.model.SubjectType;
import it.grid.storm.authz.sa.model.SpacePermission;
import it.grid.storm.authz.sa.model.AceType;

public class SpaceACETextParser {

    private SpaceACE spaceACE = new SpaceACE();

/**
    public SpaceACETextParser(String aceString) throws AuthzDBReaderException {
        parseACE(aceString);
    }

    private void parseACE(String aceString) throws AuthzDBReaderException {
        String patternStr = ",";
        String[] fields = aceString.split(patternStr);
        if (fields.length!=4) throw new AuthzDBReaderException("ACEString :"+aceString+" is not well formed");
        SubjectType subjectType = parseSubjectType(fields[0]);
        Subject subject = parseSubject(fields[1]);
        SpacePermission spacePermission = parseSpacePermission(fields[2]);
        AceType aceType = parseACEType(fields[3]);
    }
  **/
 }
