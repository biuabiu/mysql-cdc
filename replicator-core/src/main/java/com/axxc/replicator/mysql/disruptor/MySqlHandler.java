package com.axxc.replicator.mysql.disruptor;
import com.axxc.replicator.mysql.data.BinlogBatchData;
import com.lmax.disruptor.EventHandler;

public interface MySqlHandler extends EventHandler<BinlogBatchData> {
	
}