////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.importer.cli;

import com.denimgroup.threadfix.importer.util.SpringConfiguration;
import com.denimgroup.threadfix.importer.util.ScanParser;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommandLineMain {

    private static final SanitizedLogger LOGGER = new SanitizedLogger(CommandLineMain.class);

    public static void main(String[] args) {


        long startTime = System.currentTimeMillis();
        CommandLineMain main = SpringConfiguration.getContext().getBean(CommandLineMain.class);
        LOGGER.info("Initialization finished in " + (System.currentTimeMillis() - startTime) + " ms");

        String path = "/Users/gabrieleyyi/Cloud/Dropbox/01_MSE/06_VT2/07_Daten_3/big_threadfix_importer/scanner_4";
         //String path = args[0];
        String[] xmlFiles = main.processAll(path);

        // main.mainWithSpring(args);
    }

    public void mainWithSpring(String[] args) {
        if (check(args)) {
            long startTime = System.currentTimeMillis();

            String output = SpringConfiguration.getContext().getBean(ScanParser.class).readFile(args[0]);

            LOGGER.info("Scan parsing finished in " + (System.currentTimeMillis() - startTime) + " ms");
            System.out.println(output);
        }
    }

    private static boolean check(String[] args) {
        if (args.length != 1) {
            System.out.println("This program accepts one argument, the scan file to be scanned.");
            return false;
        }

        File scanFile = new File(args[0]);

        if (!scanFile.exists()) {
            System.out.println("The file must exist.");
            return false;
        }

        if (scanFile.isDirectory()) {
            System.out.println("The file must not be a directory.");
            return false;
        }

        return true;
    }


    public String[] processAll(String reportDir){
        String path = reportDir;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<String> xmlFiles = new ArrayList<>();
        String xmlPath = "";

        for(int i=0; i<listOfFiles.length;i++){
            String currentFile = listOfFiles[i].getName();
            if(currentFile.contains(".xml") || currentFile.contains(".zip")){
                xmlPath = listOfFiles[i].getAbsolutePath();
                LOGGER.info(xmlPath);
                String output = SpringConfiguration.getContext().getBean(ScanParser.class).readFile(xmlPath);
                try {
                    Files.write(Paths.get(xmlPath+"_.csv"), output.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                xmlFiles.add(xmlPath);
            }
        }

        String[] xmlArr = new String[xmlFiles.size()];
        xmlArr = xmlFiles.toArray(xmlArr);
        return xmlArr;
    }

}
