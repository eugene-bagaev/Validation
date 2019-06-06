package project.Rules;

public class Constants {
//   for TRIGGER
    public static final String TRIGGER_OBJECT_SUCCESS_MESSAGE = "Триггер создан для нужного объекта";
    public static final String TRIGGER_OBJECT_ERROR_MESSAGE = "Триггер создан не для требуемого объекта";
    public static final String TRIGGER_EVENT_ERROR_MESSAGE = "В триггере не использовано событие ";
    public static final String TRIGGER_EVENT_SUCCESS_MESSAGE = " события использованы";
    public static final String TRIGGER_HELPER_SUCCESS_MESSAGE = "Вспомогательный класс создан и использован";
    public static final String TRIGGER_HELPER_ERROR_MESSAGE = "Не создан вспомогательный класс или неправильно назван";
//   for TEST
    public static final String TEST_SUCCESS_BUT_NOT_ENOUGH_COVERAGE =  "Tests: {0} SUCCESS but not enough coverage: {1} %";
    public static final String TEST_SUCCESS =  "Tests: {0} SUCCESS with coverage: {1} %";
    public static final String TEST_NOT_FOUND =  "Tests: {0} not found";
    public static final String TEST_SUCCESS_ASSERT =  "Tests: {0} использовалось  assert функция";
    public static final String TEST_FAIL_ASSERT =  "Tests: {0} НЕ использовалось  assert функция";
    public static final String TEST_FAILED_MESSAGE = "Tests: {0} не выполнился";
//   for ApexClass
    public static final String APEXCLASS_NOT_FOUND_METHOD =  "У класса: {0} не найден метод: {1}";
    public static final String APEXCLASS_FOUND_METHOD =  "Найден метод: {1} у класса: {0}";
//   for sObject
    public static final String SOBJECT_NOT_FOUND_FIELD =  "Не найдено поле: {0} у обьекта: {1}";
    public static final String SOBJECT_FOUND_FIELD =  "Найдено поле: : {0} у обьекта: {1}";
    public static final String SOBJECT_NOT_FOUND__PROPERTY =  "Некорректное свойство: {0}  у поля  {1} у обьекта  {2} ";
    public static final String SOBJECT_FOUND__PROPERTY =  "Корректное свойство: {0}  у поля  {1} у обьекта  {2} ";
    public static final String SOBJECT_NOT_FOUND_VALIDATIONRULES =  "Не найдены validationRules: {0} у обьекта: {1} ";
    public static final String SOBJECT_FOUND_VALIDATIONRULES =  "Найдены validationRules: {0} у обьекта: {1}";
    public static final String SOBJECT_WRONG_VALIDATIONRULES_FORMULA =  "Некоректная формула у validationRules: {0}, не найдено поле: {1}";
    public static final String SOBJECT_NOT_FOUND_LABEL =  "Не найдена Label: {0} у обьекта: {1}";
    public static final String SOBJECT_FOUND_LABEL =  "Найдена Label: {0} у обьекта: {1}";


    //   for VF pages
    public static final String VF_NOT_FOUND_TAG =  "В VisualforcePage: {0} не найден тэг {1}";
    public static final String VF_FOUND_VALUE_IN_TAG =  "В VisualforcePage: {0} в теге: {1} найдено значение {2}";
    public static final String VF_NOT_FOUND_VALUE_IN_TAG =  "В VisualforcePage: {0} в теге: {1} не найдено значение {2}";
    //   for METHOD
    public static final String METHOD_SUCCESS =  "Метод: {0} в классе {1} успешно выполнился";
    public static final String METHOD_EXECUTE_FAIL =  "Метод: {0}  в классе {1}  выполнился с ошибкой";
    public static final String METHOD_NOT_COMPILE =  "Метод {0} в в классе {1} не скомпилировался";


    public static final String FROM_EMAIL_ADDRESS = "tesk.checker@vrp.com";
    public static final String TO_EMAIL_ADDRESS = "vrpvalidationtask2019@gmail.com";

    public static final String CONNECTION_EX_MESSAGE = "Connection exception occur. User: {0}. Exception text: {1}";
    public static final String RETRIEVE_EX_MESSAGE = "Retrieve zip exception occur. User: {0}. Exception text: {1}";

    public static final String FEEDBACK_DOCUMENT_ID = "1GLBSicy1cTK5nOiA3XHDf1PxI7FBP_DH0pqaQUP3itA";

}
