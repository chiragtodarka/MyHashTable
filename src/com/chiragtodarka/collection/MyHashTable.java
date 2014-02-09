package com.chiragtodarka.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MyHashTable<K,V>
{
	private final int INITAL_SIZE = 16;
	private final float LOAD_FACTOR = 0.75f;
	
	private int arraySize = INITAL_SIZE;
	private int numberOfEmptySlots = INITAL_SIZE;
	
	private int size = 0;
	
	@SuppressWarnings("unchecked")
	private ArrayList<Data<K,V>>[] dataArray= new ArrayList[INITAL_SIZE];
	
	public synchronized int getNumberOfEmptySlots() {
		return numberOfEmptySlots;
	}

	public synchronized int getArraySize() {
		return arraySize;
	}

	public synchronized int getSize() {
		return size;
	}

	public synchronized int getINITAL_SIZE() {
		return INITAL_SIZE;
	}

	public synchronized float getLOAD_FACTOR() {
		return LOAD_FACTOR;
	}

	public synchronized void put(K key, V value)
	{
		if(key==null)
			throw new NullPointerException("Key value cannot be null");
		
		//Rehashing Code::
		if(numberOfEmptySlots<=((1-LOAD_FACTOR)*arraySize))
		{
			rehash();
		}
		
		int index = key.hashCode()%arraySize;
		if(index<0)
			index = index*-1;
		
		if(dataArray[index]==null)
		{
			dataArray[index] = new ArrayList<Data<K,V>>();
			numberOfEmptySlots--;
			
			dataArray[index].add(new Data<K,V>(key, value));
			this.size++;
		}
		else
		{
			Data<K,V> data = getData(key);
			if(data==null)
			{
				dataArray[index].add(new Data<K,V>(key, value));
				this.size++;
				return;
			}
			
			data.setValue(value);
		}

	}
	
	@SuppressWarnings("unchecked")
	private synchronized void rehash()
	{
		int newArraySize = this.arraySize*2;
		int newNumberOfEmptySlots = newArraySize;
		int newSize = 0;
		ArrayList<Data<K,V>>[] newDataArray= new ArrayList[newArraySize];
		
		Set<K> keySet = this.keySet();
		Iterator<K> iterator = keySet.iterator();
		
		while(iterator.hasNext())
		{
			K key = iterator.next();
			Data<K,V> data = this.getData(key);
			
			if(key==null)
				throw new NullPointerException("Key value cannot be null");
			
			int index = key.hashCode()%newArraySize;
			if(index<0)
				index = index*-1;
			
			if(newDataArray[index]==null)
			{
				newDataArray[index] = new ArrayList<Data<K,V>>();
				newNumberOfEmptySlots--;
			}
			newDataArray[index].add(data);
			newSize++;
		}
		
		this.arraySize = newArraySize;
		this.numberOfEmptySlots = newNumberOfEmptySlots;
		this.size = newSize;
		this.dataArray = newDataArray;
		
	}
	
	private synchronized Data<K,V> getData(K key)
	{
		if(key==null)
			throw new NullPointerException("Key value cannot be null");
		
		int index = key.hashCode()%arraySize;
		if(index<0)
			index = index*-1;
		
		ArrayList<Data<K,V>> arrayList = dataArray[index];
		
		if(arrayList==null)
			throw new NullPointerException();
			
		for(int i=0; i<arrayList.size(); i++)
		{
			Data<K,V> data = arrayList.get(i);
			if(data.getKey().equals(key))
				return data;
		}
		return null;
	}
	
	public synchronized V get(K key)
	{
		if(key==null)
			throw new NullPointerException("Key value cannot be null");
		
		int index = key.hashCode()%arraySize;
		if(index<0)
			index = index*-1;
		
		if(dataArray[index]==null || dataArray[index].size()==0)
			return null;
		
		ArrayList<Data<K, V>> arrayList = dataArray[index];
		for(int i=0; i<arrayList.size(); i++)
		{
			Data<K, V> data = arrayList.get(i);
			if(data.getKey().equals(key))
				return data.getValue();
		}
		
		return null;
	}
	
	public synchronized V remove(K key)
	{
		if(key==null)
			throw new NullPointerException("Key value cannot be null");
		
		int index = key.hashCode()%arraySize;
		if(index<0)
			index = index*-1;
		
		ArrayList<Data<K,V>> arrayList = dataArray[index];
		
		if(arrayList==null)
			return null;
		
		for(int i=0; i<arrayList.size(); i++)
		{
			Data<K,V> data = arrayList.get(i);
			if(data.getKey().equals(key))
			{
				V value = data.getValue();
				arrayList.remove(i);
				this.size--;
				return value;
			}
		}
		
		return null;
	}
	
	public synchronized Set<K> keySet()
	{
		Set<K> keySet = new HashSet<K>();
		
		for(int i=0; i<this.arraySize; i++)
		{
			ArrayList<Data<K, V>> arrayList = this.dataArray[i];
			if(arrayList!=null && arrayList.size()>0)
			{
				for(int j=0; j<arrayList.size(); j++)
				{
					keySet.add(arrayList.get(j).getKey());
				}
			}
		}
		
		return keySet;
	}
	
}

class Data<K, V>
{
	private final K key;
	private V value;
	
	public Data(K key, V value) 
	{
	    this.key = key;
	    this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
}