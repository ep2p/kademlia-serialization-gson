package io.ep2p.kademlia.serialization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.BigIntegerExternalNode;
import io.ep2p.kademlia.node.external.ExternalNode;
import io.ep2p.kademlia.protocol.message.*;
import io.ep2p.kademlia.serialization.api.MessageSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collections;


public class SerializationTest {
    private static MessageSerializer<BigInteger, IPPortConnectionInfo> messageSerializer;
    private static Node<BigInteger, IPPortConnectionInfo> node = null;
    private static GsonBuilder gsonBuilder;

    @BeforeAll
    public static void initGson(){
        gsonBuilder = new GsonFactory.DefaultGsonFactory<BigInteger, IPPortConnectionInfo, String, String>(
                BigInteger.class,
                IPPortConnectionInfo.class,
                String.class,
                String.class
        ).gsonBuilder();
        messageSerializer = new GsonMessageSerializer<>(gsonBuilder);
        node = new GenericExternalNode<>(BigInteger.valueOf(1L), new IPPortConnectionInfo("localhost", 8000));
    }

    @Test
    void testDHTLookupSerialization(){
        DHTLookupKademliaMessage<BigInteger, IPPortConnectionInfo, String> kademliaMessage = new DHTLookupKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupKademliaMessage.DHTLookup<>(node, "key", 1));
        kademliaMessage.setNode(node);


        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, IPPortConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, IPPortConnectionInfo, String>>>() {}.getType();
        KademliaMessage<BigInteger, IPPortConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, IPPortConnectionInfo, String>> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
        Assertions.assertEquals(kademliaMessage1.getData().getCurrentTry(), kademliaMessage.getData().getCurrentTry());
        Assertions.assertEquals(kademliaMessage1.getData().getKey(), kademliaMessage.getData().getKey());
        Assertions.assertEquals(kademliaMessage1.getData().getRequester().getId(), kademliaMessage.getData().getRequester().getId());
    }

    @Test
    void testDHTLookUpSerialization(){
        DHTLookupResultKademliaMessage<BigInteger, IPPortConnectionInfo, String, String> kademliaMessage = new DHTLookupResultKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupResultKademliaMessage.DHTLookupResult<>(LookupAnswer.Result.FOUND, "key", "value"));
        kademliaMessage.setNode(node);


        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable>>(){}.getType();
        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testDHTCustomStoreSerialization() throws NoSuchFieldException, IllegalAccessException {

        GsonBuilder gsonBuilder = new GsonFactory.DefaultGsonFactory<>(
                BigInteger.class,
                IPPortConnectionInfo.class,
                String.class,
                CustomData.class
        ).gsonBuilder();
        MessageSerializer<BigInteger, IPPortConnectionInfo> messageSerializer = new GsonMessageSerializer<>(gsonBuilder);

        DHTStoreKademliaMessage<BigInteger, IPPortConnectionInfo, String, CustomData> kademliaMessage = new DHTStoreKademliaMessage<>();
        DHTStoreKademliaMessage.DHTData<BigInteger, IPPortConnectionInfo, String, CustomData> data = new DHTStoreKademliaMessage.DHTData<>(node, "key", new CustomData("hi"));
        System.out.println(data.getValue());
        kademliaMessage.setData(data);
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Field field = kademliaMessage1.getData().getClass().getDeclaredField("key");
        field.setAccessible(true);
        Object key = field.get(kademliaMessage1.getData());
        Assertions.assertEquals(kademliaMessage.getData().getKey(), key);

        field = kademliaMessage1.getData().getClass().getDeclaredField("value");
        field.setAccessible(true);
        Object value = field.get(kademliaMessage1.getData());
        Assertions.assertEquals(kademliaMessage.getData().getValue(), value);
    }

    @Test
    void testDHTStoreSerialization() throws NoSuchFieldException, IllegalAccessException {
        DHTStoreKademliaMessage<BigInteger, IPPortConnectionInfo, String, String> kademliaMessage = new DHTStoreKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreKademliaMessage.DHTData<>(node, "key", "value"));
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Field field = kademliaMessage1.getData().getClass().getDeclaredField("key");
        field.setAccessible(true);
        Object key = field.get(kademliaMessage1.getData());
        Assertions.assertEquals(kademliaMessage.getData().getKey(), key);
    }

    @Test
    void testDHTStoreResultSerialization(){
        DHTStoreResultKademliaMessage<BigInteger, IPPortConnectionInfo, String> kademliaMessage = new DHTStoreResultKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreResultKademliaMessage.DHTStoreResult<>("key", StoreAnswer.Result.STORED));
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        System.out.println(kademliaMessage1.getNode().getClass());
        System.out.println(kademliaMessage1.getNode().getConnectionInfo());
        Assertions.assertEquals(kademliaMessage.getNode().getId(), kademliaMessage1.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testEmptyKademliaMessageSerialization(){
        EmptyKademliaMessage<BigInteger, IPPortConnectionInfo> kademliaMessage = new EmptyKademliaMessage<>();
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof EmptyKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testFindNodeRequestSerialization(){
        FindNodeRequestMessage<BigInteger, IPPortConnectionInfo> kademliaMessage = new FindNodeRequestMessage<>();
        kademliaMessage.setNode(node);
        kademliaMessage.setData(BigInteger.valueOf(100L));

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof FindNodeRequestMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testPullingStoreSerialization() throws NoSuchFieldException, IllegalAccessException {
        DHTStorePullKademliaMessage<BigInteger, IPPortConnectionInfo, String> kademliaMessage = new DHTStorePullKademliaMessage<>();

        kademliaMessage.setData(new DHTStorePullKademliaMessage.DHTStorePullData<>("key"));
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, IPPortConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStorePullKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Field field = kademliaMessage1.getData().getClass().getDeclaredField("key");
        field.setAccessible(true);
        Object key = field.get(kademliaMessage1.getData());
        Assertions.assertEquals(kademliaMessage.getData().getKey(), key);
    }

    @Test
    void testExternalNodeSerialization(){
        ExternalNode<BigInteger, IPPortConnectionInfo> externalNode = new BigIntegerExternalNode<>(node, BigInteger.valueOf(1L));
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(externalNode);
        System.out.println(json);
        Type type = new TypeToken<ExternalNode<BigInteger, IPPortConnectionInfo>>(){}.getType();
        ExternalNode<BigInteger, IPPortConnectionInfo> externalNode1 = gson.fromJson(json, type);
        Assertions.assertNotNull(externalNode1);
        Assertions.assertEquals(externalNode.getId(), externalNode1.getId());
        Assertions.assertEquals(externalNode.getDistance(), externalNode1.getDistance());
    }

    @Test
    void testFindNodeAnswerSerialization(){
        ExternalNode<BigInteger, IPPortConnectionInfo> externalNode = new BigIntegerExternalNode<>(node, BigInteger.valueOf(1L));

        FindNodeAnswer<BigInteger, IPPortConnectionInfo> findNodeAnswer = new FindNodeAnswer<>(BigInteger.valueOf(1L));
        findNodeAnswer.setNodes(Collections.singletonList(new BigIntegerExternalNode<>(externalNode, BigInteger.valueOf(100L))));

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(externalNode);
        System.out.println(json);

    }
}
