package com.dpcsa.compon.interfaces_classes;

public class WeekData {
    public int week, type, month, year, select;
    public int[] days, typeRange;   // 0 - вне диапазона, 1 - первый день диапазона, 2 - последний день диапазона,
    // 3 - все остальные дни диапазона, 4 - первый и последний дни совпадают.
    public int[] typeDays;          // 0 - рабочий, 9 - числа вне основных, 10 - нет даты

    public WeekData(int year, int month, int type) {
        this.year = year;
        this.month = month;
        this.type = type;
        days = new int[7];
        typeRange = new int[7];
        typeDays = new int[7];
        if (type == 1) {
            for (int i = 0; i < 7; i++) {
                days[i] = -1;
                typeRange[i] = 0;
                typeDays[i] = 10;
            }
        }
    }
}
