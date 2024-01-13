package ru.pevnenko;

/**
 * Парсинг SQL запросов на налчиие комментариев
 * на основе графа состояний
 */
public class StateGraph {

    enum State {
        POSSIBLE_INLINE_COMMENT,
        INLINE_COMMENT,
        POSSIBLE_START_MULTI_COMMENT,
        POSSIBLE_END_MULTI_COMMENT,
        MULTI_COMMENT,
        UNKNOWN;
    }


    public static char firstCharSQLQuery(String query) {

        //Начальное состояние
        State state = State.UNKNOWN;
        char buffer = 0;
        for (int i = 0; i < query.length(); i++) {
            char symbol = query.charAt(i);

            if (state == State.UNKNOWN) {
                //проверка на спец символы
                if (symbol == ' ' || symbol == '\n' || symbol == '\r' || symbol == '\t')
                    continue;

                //Проверка на однострочный комментарий
                if (symbol == '-') {
                    state = State.POSSIBLE_INLINE_COMMENT;
                    continue;
                }

                //Проверка на многостррочный комментарий
                if (symbol == '/') {
                    state = State.POSSIBLE_START_MULTI_COMMENT;
                    continue;
                }

                //Если все проверки завершились , то нашли нужный символ
                buffer = symbol;
                break;
            }

            if (state == State.POSSIBLE_INLINE_COMMENT) {
                //все до переноса строки нам не нужно
                if (symbol == '-') {
                    state = State.INLINE_COMMENT;
                    continue;
                }
                break;
            }

            if (state == State.INLINE_COMMENT) {
                if (symbol == '\n') {
                    state = State.UNKNOWN;
                    continue;
                }
            }

            if (state == State.POSSIBLE_START_MULTI_COMMENT) {
                if (symbol != '*') break;

                state = State.MULTI_COMMENT;
                continue;
            }

            //Проверяем конец мулттистрочного комментария
            if (state == State.MULTI_COMMENT) {
                if (symbol == '*') {
                    state = State.POSSIBLE_END_MULTI_COMMENT;
                    continue;
                }
            }

            // Если многострочный комментарий закрыт ложно
            if (state == State.POSSIBLE_END_MULTI_COMMENT) {
                if (symbol != '/') {
                    state = State.MULTI_COMMENT;
                } else {
                    state = State.UNKNOWN;
                }
            }

        }

        return Character.toLowerCase(buffer);

    }
}
