package com.k2view.cdbms.usercode.common;

import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.sync.*;
import com.k2view.broadway.model.Actor;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.fabric.common.Util;
import com.k2view.fabric.common.mtable.MTable;
import com.k2view.fabric.common.mtable.MTables;
import com.k2view.broadway.model.Data;


public class MTableShift implements Actor {

	private static final String MTABLE_PARAM = "mtable";
	private static final String CASE_SENSITIVE_PARAM = "mtableCaseSensitive";
	private static final String KEY_PARAM = "mtableKey";
	private static final String RANDOM_PARAM = "mtableRandomRow";
	private AutoCloseable needClose = null;

	public MTableShift() {
	}

	public void action(Data input, Data output) throws Exception {
		Util.safeClose(this.needClose);
		this.needClose = null;
		MTable mtable = MTables.get(input.string("mtable"));
		long seed = input.integer("seed");
		String originalValue = input.string("value");
		String keyParam = input.string("mtableKey");

		Iterator<Map<String, Object>> itr = mtable.allMaps().iterator();
		List<Object> values = new ArrayList<>();

		int indexFound=0;
		int index = 0;
		while(itr.hasNext()){
			Map<String, Object> map = itr.next();
			Object rawValue = map.get(keyParam);
			values.add(rawValue);

			String stringValue = String.valueOf(rawValue);
			if(stringValue.trim().equals(originalValue)){
				indexFound = index;
			}
			index++;
		}
		long indexAfterShift = (indexFound + seed) % values.size();
		Object result = values.get((int)indexAfterShift);
		output.put("result", result);

	}

	public void close() throws Exception {
		Util.safeClose(this.needClose);
	}

}