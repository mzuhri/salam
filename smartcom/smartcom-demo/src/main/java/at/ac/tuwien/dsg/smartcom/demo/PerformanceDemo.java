package at.ac.tuwien.dsg.smartcom.demo;

import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.adapter.InputPushAdapter;
import at.ac.tuwien.dsg.smartcom.adapter.PushTask;
import at.ac.tuwien.dsg.smartcom.adapter.annotations.Adapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceDemo {

    private final static int TOTAL_MESSAGES = 100000;

    public static void main(String[] args) throws IOException, CommunicationException, BrokenBarrierException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Select the number of peers:");
        int peersAmount = Integer.valueOf(reader.readLine().trim());

        System.out.println("Select the number of concurrent message producers:");
        int messageProducersAmount = Integer.valueOf(reader.readLine().trim());

        DemoPeerManager peerManager = new DemoPeerManager();
        for (int i = 0; i < peersAmount; i++) {
            Identifier id = Identifier.peer("peer"+i);
            List<PeerChannelAddress> addresses = new ArrayList<>();

            List<Serializable> parameters = new ArrayList<>(1);
            PeerChannelAddress address = new PeerChannelAddress(id, Identifier.channelType("adapter"), parameters);
            addresses.add(address);

            peerManager.addPeer(id, new PeerInfo(id, DeliveryPolicy.Peer.TO_ALL_CHANNELS, null, addresses), id.getId());
        }

        int messages_per_worker_per_peer = TOTAL_MESSAGES/messageProducersAmount/peersAmount;
        if (messages_per_worker_per_peer == 0) {
            messages_per_worker_per_peer = 1;
        }

        int messages = messageProducersAmount*peersAmount* messages_per_worker_per_peer;
        CountDownLatch counter = new CountDownLatch(messages);

        SmartCom smartCom = new SmartCom(peerManager, peerManager, peerManager);
        smartCom.initializeSmartComWithoutAdapters();
        Communication communication = smartCom.getCommunication();
        communication.registerNotificationCallback(new NotificationHandler(counter));
        communication.registerOutputAdapter(OutputAdapter.class);

        CyclicBarrier barrier = new CyclicBarrier(messageProducersAmount+1);

        for (int i = 0; i < 10; i++) {
            communication.addPushAdapter(new InputAdapter());
        }

        for (int i = 0; i < messageProducersAmount; i++) {
            Message.MessageBuilder builder = new Message.MessageBuilder()
                    .setType("COMPUTE")
                    .setSubtype("REQUEST")
                    .setSenderId(Identifier.component("DEMO"))
                    .setConversationId(System.nanoTime() + "")
                    .setContent("Do some stuff and respond!");
            new Thread(new WorkerThread(builder, barrier, communication, peersAmount, messages_per_worker_per_peer)).start();
        }

        System.out.println("START");
        long start = System.currentTimeMillis();
        barrier.await();

        while (!counter.await(10, TimeUnit.SECONDS)) {
            int count = (int) counter.getCount();
            long end = System.currentTimeMillis();
            long diff = end-start;
            System.out.println("Messages left: " + count + "/" + messages + " ("+(((float)(messages-count))/(((float)diff)/1000f))*2+") "+sentMessages.get());
        }
        long end = System.currentTimeMillis();
        System.out.println("END");

        long diff = end-start;

        System.out.println("Duration: "+diff+" milliseconds");
        System.out.println("Messages: "+messages);
        System.out.println("Messages per seconds: "+(((float)messages)/(((float)diff)/1000f))*2);

        smartCom.tearDownSmartCom();
    }

    private static class WorkerThread implements Runnable {

        private final Message msg;
        private final CyclicBarrier barrier;
        private final Communication communication;
        private final int peers;
        private final int messages_per_worker_per_peer;

        private WorkerThread(Message.MessageBuilder builder, CyclicBarrier barrier, Communication communication, int peers, int messages_per_worker_per_peer) {
            this.msg = builder.create();
            this.barrier = barrier;
            this.communication = communication;
            this.peers = peers;
            this.messages_per_worker_per_peer = messages_per_worker_per_peer;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }

            for (int j = 0; j < messages_per_worker_per_peer; j++) {
                for (int i = 0; i < peers; i++) {
                    try {
                        msg.setReceiverId(Identifier.peer("peer" + i));
                        communication.send(msg.clone());
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Worker done sending "+ messages_per_worker_per_peer * peers+" messages");
        }
    }

    private static class NotificationHandler implements NotificationCallback {

        private final CountDownLatch counter;

        public NotificationHandler(CountDownLatch counter) {
            this.counter = counter;
        }

        @Override
        public void notify(Message message) {
            if ("RESPONSE".equals(message.getSubtype())) {
                counter.countDown();
                receivedMessages.incrementAndGet();
            } else {
                System.out.println(message.toString());
            }
        }
    }

    private static AtomicInteger sentMessages = new AtomicInteger();
    private static AtomicInteger receivedMessages = new AtomicInteger();
    private static BlockingDeque<Message> queue = new LinkedBlockingDeque<>();

    @Adapter(name="adapter", stateful = true)
    public static class OutputAdapter implements at.ac.tuwien.dsg.smartcom.adapter.OutputAdapter {

        @Override
        public void push(Message message, PeerChannelAddress address) {
            sentMessages.incrementAndGet();
            queue.push(message);
        }
    }

    public static class InputAdapter extends InputPushAdapter {

        private boolean run = true;

        @Override
        protected void cleanUp() {
            run = false;
        }

        @Override
        public void init() {
            schedule(new PushTask() {
                @Override
                public void run() {
                    while (run) {
                        try {
                            Message message = queue.take();
                            publishMessage(new Message.MessageBuilder()
                                    .setType("COMPUTE")
                                    .setSubtype("RESPONSE")
                                    .setSenderId(Identifier.adapter("adapter"))
                                    .setConversationId(message.getConversationId())
                                    .setContent("Do some stuff and respond!")
                                    .create());
                        } catch (InterruptedException e) {
                            run = false;
                        }
                    }
                }
            });
        }
    }
}