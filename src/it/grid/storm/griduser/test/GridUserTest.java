package it.grid.storm.griduser.test;

import it.grid.storm.griduser.DistinguishedName;

public class GridUserTest {
    
    public static void main(String[] args) {

        String dnservizio = "/DC=org/DC=doegrids/OU=Services/CN=sam/cdfsam1.cr.cnaf.infn.it";
        
        DistinguishedName dn =  new DistinguishedName(dnservizio);
        
        System.out.println(dn.toString());
        
        
    }
}
