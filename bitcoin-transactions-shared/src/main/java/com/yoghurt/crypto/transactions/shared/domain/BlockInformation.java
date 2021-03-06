package com.yoghurt.crypto.transactions.shared.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BlockInformation implements Serializable, IsSerializable {
  private static final long serialVersionUID = 4582485308423158376L;

  private String rawBlockHeaders;

	private int height;
	private int numConfirmations;
	private int numTransactions;
	private long size;
	private String nextBlockHash;
	
	private long strippedSize;
	private long weight;

	private TransactionInformation coinbaseInformation;

	public BlockInformation() {
	}

	public String getRawBlockHeaders() {
		return rawBlockHeaders;
	}

	public void setRawBlockHeaders(final String rawBlockHeaders) {
		this.rawBlockHeaders = rawBlockHeaders;
	}

	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public String getNextBlockHash() {
		return nextBlockHash;
	}

	public void setNextBlockHash(final String nextBlockHash) {
		this.nextBlockHash = nextBlockHash;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public void setNumConfirmations(final int numConfirmations) {
		this.numConfirmations = numConfirmations;
	}

	public int getNumConfirmations() {
		return numConfirmations;
	}

	public int getNumTransactions() {
		return numTransactions;
	}

	public void setNumTransactions(final int numTransactions) {
		this.numTransactions = numTransactions;
	}

	public TransactionInformation getCoinbaseInformation() {
		return coinbaseInformation;
	}

	public void setCoinbaseInformation(final TransactionInformation coinbaseInformation) {
		this.coinbaseInformation = coinbaseInformation;
	}

  public long getStrippedSize() {
    return strippedSize;
  }

  public void setStrippedSize(long strippedSize) {
    this.strippedSize = strippedSize;
  }

  public long getWeight() {
    return weight;
  }

  public void setWeight(long weight) {
    this.weight = weight;
  }

	@Override
	public String toString() {
		return "BlockInformation [rawBlockHeaders=" + rawBlockHeaders + ", height=" + height + ", numConfirmations="
				+ numConfirmations + ", numTransactions=" + numTransactions + ", size=" + size + ", nextBlockHash="
				+ nextBlockHash + ", coinbaseInformation=" + coinbaseInformation + "]";
	}
}
