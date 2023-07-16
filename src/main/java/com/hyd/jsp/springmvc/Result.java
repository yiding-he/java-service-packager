package com.hyd.jsp.springmvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hyd.jsp.utils.Jackson;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 表示执行/查询结果的通用结构
 * created at 2015/6/28
 *
 * @author Yiding
 */
public class Result implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static int DEFAULT_SUCCESS = 0;

  private static int DEFAULT_FAIL = -1;

  private int resultCode;     // 结果代码，通常 0 表示成功

  private String message;      // 相关信息，通常用于展示错误信息

  private Map<String, Object> data = new HashMap<>();   // 返回值

  private String errorStackTrace;        // 相关异常信息

  ////////////////////////////////////////////////////////////////

  public Result() {
  }

  public Result(int resultCode, String message) {
    this.resultCode = resultCode;
    this.message = message;
  }

  public Result(int resultCode, String message, Throwable exception) {
    this.resultCode = resultCode;
    this.message = message;
    this.errorStackTrace = exception == null ? null : ExceptionUtils.getStackTrace(exception);
  }

  public Result(Result original) {
    this.resultCode = original.resultCode;
    this.message = original.message;
    this.data = original.data == null ? new HashMap<>() : original.data;
    this.errorStackTrace = original.errorStackTrace;
  }

  /**
   * 获得缺省的失败代码，注意这个设置是全局的
   *
   * @return 缺省的失败代码
   */
  public static int getDefaultFail() {
    return DEFAULT_FAIL;
  }

  /**
   * 获得缺省的失败代码，注意这个设置是全局的
   *
   * @param defaultFail 缺省的失败代码
   */
  public static void setDefaultFail(int defaultFail) {
    DEFAULT_FAIL = defaultFail;
  }

  public static int getDefaultSuccess() {
    return DEFAULT_SUCCESS;
  }

  public static void setDefaultSuccess(int defaultSuccess) {
    Result.DEFAULT_SUCCESS = defaultSuccess;
  }

  /////////////////////////////////////////////////////////////////

  public static Result success() {
    return new Result();
  }

  public static Result success(String message) {
    return new Result(DEFAULT_SUCCESS, message);
  }

  /////////////////////////////////////////////////////////////////

  public static Result fail(String message) {
    return fail(DEFAULT_FAIL, message, null);
  }

  public static Result fail(Throwable throwable) {
    return fail(DEFAULT_FAIL, throwable.getMessage(), throwable);
  }

  public static Result fail(String message, Throwable throwable) {
    return fail(DEFAULT_FAIL, message, throwable);
  }

  public static Result fail(int resultCode, String message) {
    return fail(resultCode, message, null);
  }

  public static Result fail(int resultCode, String message, Throwable exception) {
    if (resultCode == DEFAULT_SUCCESS) {
      throw new IllegalArgumentException("失败的代码不能与 DEFAULT_SUCCESS 相同");
    }
    return new Result(resultCode, message, exception);
  }

  ////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    return "Result{" +
      "resultCode=" + resultCode +
      ", message='" + message + '\'' +
      ", data=" + data +
      ", exception=" + errorStackTrace +
      '}';
  }

  ////////////////////////////////////////////////////////////////

  public Map<String, Object> getData() {
    return data;
  }

  /**
   * 设置内容。注意：在这之后不管如何修改 data，Result 都不受影响。
   */
  public void setData(Map<String, Object> data) {
    if (data != null) {
      this.data = new HashMap<>(data);
    }
  }

  /**
   * 设置值
   *
   * @param key   键
   * @param value 值
   *
   * @return Result 对象自身
   */
  public Result set(String key, Object value) {
    if (this.data == null) {
      this.data = new HashMap<>();
    }
    this.data.put(key, value);
    return this;
  }

  /**
   * 设置内容。注意：在这之后不管如何修改 data，Result 都不受影响。
   */
  public Result setAll(Map<String, Object> data) {
    setData(data);
    return this;
  }

  /**
   * 合并参数到当前内容。注意：在这之后不管如何修改 data，Result 都不受影响。
   */
  public Result mergeAll(Map<String, Object> data) {
    if (this.data == null && data != null) {
      setData(data);
    } else if (this.data != null && data != null) {
      this.data.putAll(data);
    }
    return this;
  }

  /////////////////////////////////////////////////////////////

  /**
   * 获得值
   *
   * @param key 键
   * @param <T> 值类型
   *
   * @return 值，如果没有则返回 null
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return this.data == null ? null : (T) this.data.get(key);
  }

  /**
   * 以特定类型获取返回值属性
   *
   * @param key  属性名
   * @param type 属性值
   * @param <T>  属性值类型
   *
   * @return 如果属性所代表的 JsonElement 对象能够被转化成 T 对象的话，则返回一个包含了属性值的 T 对象。
   */
  @SuppressWarnings({"unchecked", "Duplicates"})
  public <T> T getObject(String key, Class<T> type) {
    Object value = data.get(key);
    if (value == null) {
      return null;
    }

    if (type == Date.class) {

      if (value instanceof Number || value.toString().matches("\\d+")) {
        return (T) new Date(Long.parseLong(value.toString()));
      }

      try {
        return (T) FORMAT.parse(value.toString());
      } catch (ParseException e) {
        throw new RuntimeException("Property \"" + key + "\" cannot be converted to " + type, e);
      }
    } else if (type.isAssignableFrom(value.getClass())) {
      return (T) value;
    } else if (Number.class.isAssignableFrom(type)) {
      try {
        String strValue = value.toString();

        // 如果目标类型为整数，则去掉小数部分
        strValue = removeDecimalsIfNecessary(type, strValue);

        return type.getDeclaredConstructor(new Class<?>[]{String.class}).newInstance(strValue);
      } catch (Exception e) {
        throw new RuntimeException("Property \"" + key + "\" cannot be converted to " + type, e);
      }
    } else if (value instanceof Map) {
      return Jackson.deserializeMap((Map<String, Object>) value, type);
    }

    throw new RuntimeException("Property \"" + key + "\" cannot be converted to " + type);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(String key, Function<JsonNode, T> converter) {
    Object value = data.get(key);

    if (value == null) {
      return null;
    }

    if (value instanceof ArrayNode) {
      ArrayNode list = (ArrayNode) value;
      return StreamSupport.stream(list.spliterator(), false).map(converter).collect(Collectors.toList());
    } else {
      throw new IllegalArgumentException("Property '" + key + "' is not a list: " + value);
    }
  }

  @SuppressWarnings({"Duplicates", "unchecked"})
  public <T> List<T> getList(String key, Class<T> type) {
    Object value = data.get(key);

    if (value == null) {
      return null;
    }

    if (value instanceof ArrayNode) {
      ArrayNode list = (ArrayNode) value;
      return StreamSupport.stream(list.spliterator(), false).map(node -> Jackson.deserializeNode(node, type)).collect(Collectors.toList());
    } else if (value instanceof List) {
      List list = (List) value;

      // 没有指定要转换的类型
      if (type == null) {
        return (List<T>) list;
      }

      // 列表为空
      if (list.isEmpty() || list.get(0) == null) {
        return list;
      }

      Class elementType = list.get(0).getClass();

      // 元素类型已经匹配，无需转换
      if (type.isAssignableFrom(elementType)) {
        List<T> listOfType = new ArrayList<T>();
        for (Object o : list) {
          listOfType.add((T) o);
        }
        return listOfType;
      }
    }

    throw new RuntimeException("Property \"" + key + "\" is not a list");
  }

  public String getString(String key) {
    return getString(key, "");
  }

  public String getString(String key, String defaultValue) {
    try {
      Object value = this.data == null ? null : this.data.get(key);
      return value == null ? defaultValue : value.toString();
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public Integer getInteger(String key) {
    return getObject(key, Integer.class);
  }

  public int getInteger(String key, int defaultValue) {
    try {
      Integer value = getInteger(key);
      return value == null ? defaultValue : value;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public Long getLong(String key) {
    return getObject(key, Long.class);
  }

  public long getLong(String key, long defaultValue) {
    try {
      Long value = getLong(key);
      return value == null ? defaultValue : value;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public Double getDouble(String key) {
    return getObject(key, Double.class);
  }

  public double getDouble(String key, double defaultValue) {
    try {
      Double value = getDouble(key);
      return value == null ? defaultValue : value;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public Date getDate(String key) {
    return getObject(key, Date.class);
  }

  public Date getDate(String key, Date defaultValue) {
    try {
      Date value = getDate(key);
      return value == null ? defaultValue : value;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public Boolean getBoolean(String key) {
    Object value = get(key);
    return value == null ? null : Boolean.parseBoolean(value.toString());
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    try {
      Boolean value = getBoolean(key);
      return value == null ? defaultValue : value;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  // 如果 type 为整数，则去掉 strValue 的小数部分
  private <T> String removeDecimalsIfNecessary(Class<T> type, String strValue) {
    int dotIndex = strValue.indexOf(".");
    if ((type == Integer.class || type == Long.class) && dotIndex >= 0) {
      strValue = strValue.substring(0, dotIndex);
    }
    return strValue;
  }

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSuccess() {
    return this.resultCode == DEFAULT_SUCCESS;
  }

  public String getErrorStackTrace() {
    return errorStackTrace;
  }

  public void setErrorStackTrace(String errorStackTrace) {
    this.errorStackTrace = errorStackTrace;
  }

  //////////////////////////////////////////////////////////////

  public Result whenFail(Runnable runnable) {
    if (!isSuccess() && runnable != null) {
      runnable.run();
    }
    return this;
  }

  public Result whenFail(Consumer<Result> consumer) {
    if (!isSuccess() && consumer != null) {
      consumer.accept(this);
    }
    return this;
  }

  public Result failThenThrow(Supplier<RuntimeException> exProducer) {
    if (!isSuccess() && exProducer != null) {
      throw exProducer.get();
    }
    return this;
  }

  public Result failThenThrow(Function<Result, RuntimeException> exProducer) {
    if (!isSuccess() && exProducer != null) {
      throw exProducer.apply(this);
    }
    return this;
  }

  public Result whenSuccess(Runnable runnable) {
    if (isSuccess() && runnable != null) {
      runnable.run();
    }
    return this;
  }

  public Result whenSuccessThen(Supplier<Result> nextResultSupplier) {
    if (isSuccess() && nextResultSupplier != null) {
      return nextResultSupplier.get();
    }
    return this;
  }

  public Result whenSuccess(Consumer<Result> consumer) {
    if (isSuccess() && consumer != null) {
      consumer.accept(this);
    }
    return this;
  }

  public <T> T succeedThenReturn(Supplier<T> supplier) {
    if (isSuccess() && supplier != null) {
      return supplier.get();
    } else {
      return null;
    }
  }

  public <T> T succeedThenReturn(Function<Result, T> supplier) {
    if (isSuccess() && supplier != null) {
      return supplier.apply(this);
    } else {
      return null;
    }
  }

  public <T> T failThenReturn(Supplier<T> supplier) {
    if (!isSuccess() && supplier != null) {
      return supplier.get();
    } else {
      return null;
    }
  }

  public <T> T failThenReturn(Function<Result, T> supplier) {
    if (!isSuccess() && supplier != null) {
      return supplier.apply(this);
    } else {
      return null;
    }
  }
}
