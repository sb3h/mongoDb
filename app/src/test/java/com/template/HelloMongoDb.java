package com.template;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sam3h on 2016/7/5 0005.
 */
public class HelloMongoDb {


    @Before
    public void init() {
        // 连接到 mongodb 服务
        mongoClient = new MongoClient("localhost", 27017);


        // 连接到数据库
        mongoDatabase = mongoClient.getDatabase("mycol");
        System.out.println("Connect to database successfully");

        collection = mongoDatabase.getCollection("test");
        System.out.println("集合 test 选择成功");
    }

    // 连接到 mongodb 服务
    MongoClient mongoClient = null;
    MongoDatabase mongoDatabase = null;
    MongoCollection<Document> collection = null;

    @After
    public void end() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    public void createCollectionTest() {
        mongoDatabase.createCollection("test");
        System.out.println("集合创建成功");
    }

//    @Test
//    public void getCollectionTest() {
//        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
//        System.out.println("集合 test 选择成功");
//    }

    @Test
    public void insertManyCollectionTest() {


        //插入文档
        /**
         * 1. 创建文档 org.bson.Document 参数为key-value的格式
         * 2. 创建文档集合List<Document>
         * 3. 将文档集合插入数据库集合中 mongoCollection.insertMany(List<Document>) 插入单个文档可以用 mongoCollection.insertOne(Document)
         * */
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < 10; i++) {
            Document document = new Document()
                    .append("title", "MongoDB" + i)
                    .append("description", "database" + i)
                    .append("likes", 200 + i)
                    .append("by", "Fly" + i);

            documents.add(document);
        }
        collection.insertMany(documents);
        System.out.println("文档插入成功");
    }

    @Test
    public void findCollectionTest() {
        //检索所有文档
        /**
         * 1. 获取迭代器FindIterable<Document>
         * 2. 获取游标MongoCursor<Document>
         * 3. 通过游标遍历检索出的文档集合
         * */
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            System.out.println(mongoCursor.next());
        }
    }

    @Test
    public void updateCollectionTest() {
//更新文档   将文档中likes=100的文档修改为likes=200
        collection.updateMany(
                Filters.eq("likes", 200),
                new Document("$set",
                        new Document("likes", 100)));

        findCollectionTest();
    }

    @Test
    public void deleteCollectionTest() {
//更新文档   将文档中likes=100的文档修改为likes=200
        //删除符合条件的第一个文档
        collection.deleteOne(Filters.eq("likes", 100));
        //删除所有符合条件的文档
        collection.deleteMany(Filters.eq("likes", 100));

        findCollectionTest();
    }

    @Test
    public void aggregateCollectionTest() {
        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        };
        collection.aggregate(Arrays.asList(
                Aggregates.match(Filters.gte("likes", 201)),
                Aggregates.match(Filters.lte("likes", 205)),
                //hello为输出字段，expression($likes)为提取哪个字段
                Aggregates.group(null,Accumulators.sum("hello", "$likes"))
                )
        ).forEach(printBlock);

//        Document myDoc = collection.aggregate(
//                Collections.singletonList(
//                        Aggregates.group(null, Accumulators.sum("total", "$i")))).first();
//        System.out.println(myDoc.toJson());
    }



}
