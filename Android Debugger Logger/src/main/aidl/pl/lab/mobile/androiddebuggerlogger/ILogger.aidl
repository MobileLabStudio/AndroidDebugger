package pl.lab.mobile.androiddebuggerlogger;

interface ILogger {

  void log(in String messageJson);

  void logList(in List<String> jsonMessages);
}