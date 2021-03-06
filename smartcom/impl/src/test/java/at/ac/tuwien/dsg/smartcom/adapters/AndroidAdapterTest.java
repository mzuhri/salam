package at.ac.tuwien.dsg.smartcom.adapters;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This test demonstrates the behaviour of the dropbox input/output adapter.
 * Note that this is not a JUnit test and can only be run by invoking the main method.
 *
 * The test requires the user to add an access token of a dropbox account. This can be generated by
 * creating an app on <a href='https://www.dropbox.com/developers/apps'>https://www.dropbox.com/developers/apps</a>
 * and by clicking the 'Generate' button below the 'Generate access token' headline.
 *
 * The output adapter will create a new file 'task_[TIMESTAMP].task' in the folder 'smartcom' of the linked Dropbox account.
 * Afterwards the input adapter will start looking for the file and will report its existence.
 */
public class AndroidAdapterTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Please insert the registration id of an android device:");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        testAndroidOutputAdapter(code);
    }

    public static void testAndroidOutputAdapter(String regId) throws Exception {

        List<Serializable> parameters = new ArrayList<>(1);
        parameters.add(regId);
        PeerChannelAddress address = new PeerChannelAddress(Identifier.peer("test"), Identifier.adapter("Android"), parameters);

        final Message message = new Message.MessageBuilder()
                .setId(Identifier.message("testId"))
                .setContent("testContent")
                .setType("testType")
                .setSubtype("testSubType")
                .setSenderId(Identifier.peer("sender"))
                .setReceiverId(Identifier.peer("receiver"))
                .setConversationId(""+System.nanoTime())
                .setTtl(3)
                .setLanguage("testLanguage")
                .setSecurityToken("securityToken")
                .create();

        AndroidOutputAdapter adapter = new AndroidOutputAdapter();
        adapter.push(message, address);
    }
}