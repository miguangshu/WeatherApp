package com.way.fragment;

public interface ITaskManager {

	void addTask(WorkTask task);

	void removeTask(String taskId, boolean cancelIfRunning);

	void removeAllTask(boolean cancelIfRunning);
	
	int getTaskCount(String taskId);
	
}