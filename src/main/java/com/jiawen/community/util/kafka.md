可以处理TB级别的海量数据

kafka将数据(通知)永久保存到数据介质里面

硬盘价格比较低 空间大 所以可以存TB级别的数据

但是读取硬盘速度比较慢 实际上对硬盘顺序读写性能非常高 比内存随机读取效率高

具有高可靠性 因为是一个分布式的服务器 可以做集群部署，具有容错能力

具有高可扩展性 

术语
 - Broker  : kafka的服务器
 - Zookeeper : 用来管理集群，使用kafka的时候内置了独立的zookeeper
 - Topic : 消息队列实现方式: 
1.点对点的方式 比如阻塞队列 不会产生冲突(每个数据只被一个数据消费) 
2.消息可以被多个消费者读到 (订阅模式) kafka采用 
生产者将消息发到Topic 可以理解为一个文件夹(消息发布的空间)
 - Partition: 主题下面的分区 为了增强并发能力，可以创建多个分区，每一个分区从前往后按照顺序追加写入数据
 - offset 消息在分区内存在的索引
 - Replica 副本 是对数据做备份 因为Kafka是分布式对消息引擎 每一个分区有多个副本，提高容错率
副本分成主副本(Leader Replica)和随从副本(Follower Replica)，从副本只是备份，不负责做相应，某一个时刻 主副本挂掉了 集群会在从副本中选出一个当作主副本

server
vi /usr/local/etc/kafka/server.properties
zookeeper
vi /usr/local/etc/kafka/zookeeper.properties
开启zookeeper
brew services start zookeeper
开启kafka
brew services start kafka
设置topic
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
查看这个服务器上有什么主题
kafka-topics --list --bootstrap-server localhost:9092
开始发送消息
kafka-console-producer --broker-list localhost:9092 --topic test
消费者接收消息
kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning



当服务启动之后 Spring会利用方法监听配置的主题 如果主题上有消息 我们可以通过record来做消息处理

评论 点赞 关注分别是不同的Topic

一旦事件发生 我们可以将消息扔到队列里面 后续的业务是由消费者线程所处理 (异步过程)并发同时进行

消费者线程从队列中读到消息 往message表里面存一条数据

从业务角度来讲 这是一个事件驱动方式(以事件为主体解决的)
基于事件对主体进行封装
1.要定义事件对象 将事件发生所需的数据进行封装 封装内容包含消息所需要的所有数据
2.开发事件的生产者
3.开发事件的消费者

