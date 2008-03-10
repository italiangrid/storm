package it.grid.storm.namespace.admin.server;

public class AddUsersToAuthzDB {
    public AddUsersToAuthzDB() {
    }

    private void createDB() {
        NSAdminDBUtil.createDB();
        NSAdminDBUtil.shutdownDB();
    }

    /**
     * To use one time only.
     * There is a UNIQUE constraint on LoginId.
     */
    private void addUsers() {
        NSAdminDBUtil.addNewUserIntoDB("primo", "pwd1", "Nome Cognome", "Benvenuto");
        NSAdminDBUtil.addNewUserIntoDB("ritz", "ciccio", "Riccardo Zappi", "Benvenuto");

    }

    private void retrieveUser() {
        //NSAdminUtil.retrieveUser("primo", "pwd1");
        NSAdminDBUtil.retrieveUser("ritz", "ciccio");
    }

    public static void main(String[] args) {
        AddUsersToAuthzDB testns = new AddUsersToAuthzDB();
        testns.createDB();
        testns.addUsers();
        testns.retrieveUser();

    }
}
