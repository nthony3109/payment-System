package com.payement.wallet.KafkaGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducerService {
    private static  final String TOPIC = "transaction-events";
    private final KafkaTemplate<String, TransactionKafkaPayLoad> kafkaTemplate;


    public  void publishTransaction(TransactionKafkaPayLoad payLoad) {
        String Key = payLoad.fromAccountNumber() != null ? payLoad.fromAccountNumber() : payLoad.toAccountNumber();

        log.info("publishing to kafka ref: {}, type : {}",payLoad.transactionRef(),payLoad.type());
        kafkaTemplate.send(TOPIC,Key,payLoad);
    }
}
