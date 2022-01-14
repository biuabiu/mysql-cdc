package com.axxc.replicator.work.sync;

import com.axxc.replicator.mysql.data.BinlogBatchData;

public interface DataSyncAccessor {
	
	void invokeAfter(DataSyncMethod current, BinlogBatchData data, Exception e);
	
	void invokeBefore(DataSyncMethod current, BinlogBatchData data);
	
	void skip(DataSyncMethod current, DataSyncMethod last, BinlogBatchData data);
}
