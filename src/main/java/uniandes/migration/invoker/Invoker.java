package uniandes.migration.invoker;

import spoon.Launcher;

/**
 * Created by carlos on 9/23/15.
 */
public class Invoker {

    /*
     * Propertie names from the POM
     */
    private static final String PARAM_PROCESSOR = "-Processor";
    private static final String PARAM_LEGACY = "-LegacyPath";


    private String processor;
    private String legacyPath;

    /**
     * Main method, it parses parameters and invokes a spoon processor
     * @param args
     */
    public static void main (String args[]){
        Invoker invoker = new Invoker();
        invoker.parseParameters(args);
        invoker.invokeSpoon(invoker.processor, invoker.legacyPath);
    }

    /**
     * Parse parameters received from maven
     * @param args Array with the parameters sent by maven
     */
    private void parseParameters(String args[]){
        System.out.println("[INFO] Parameters for Spoon Invocation:");
        for(int i=0; i<args.length;i++){
            //System.out.println("found: "+args[i]);
            if(args[i].equals(PARAM_PROCESSOR)) {
                processor = args[i + 1];
                System.out.println("[INFO]\t "+PARAM_PROCESSOR+" -> "+processor);
            }
            else if(args[i].equals(PARAM_LEGACY)) {
                legacyPath = args[i + 1];
                System.out.println("[INFO]\t " + PARAM_LEGACY+" -> " + legacyPath);
            }
        }

    }

    /**
     * Invokes a spoon processor individually
     */
    public void invokeSpoon(String processor, String legacyPath) {
    	// Invoke spoon processor for methods
        String[] spoonArgs = new String[6];
        spoonArgs[0] = "-i";
        spoonArgs[1] = legacyPath;
        spoonArgs[2] = "-p";
        spoonArgs[3] = processor;
        spoonArgs[4] = "--compliance";
        spoonArgs[5] = "7";
        try {
            Launcher.main(spoonArgs);
        } catch (Exception e) {
            System.err.println("Error while executing spoon launcher "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
