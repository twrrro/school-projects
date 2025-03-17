package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView solutionTv, resultTv;
    private StringBuilder input = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solutionTv = findViewById(R.id.solution_tv);
        resultTv = findViewById(R.id.result_tv);

        int[] buttonIds = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.button_dot, R.id.button_open_bracket, R.id.button_close_bracket,
                R.id.buttonEqual, R.id.buttonClear, R.id.button_AC,
                R.id.button_factorial, R.id.button_us, R.id.button_karekok, R.id.button_exit};

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();

        if (buttonText.equals("AC")) {
            input.setLength(0);
            resultTv.setText("0");
        } else if (buttonText.equals("C")) {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            }
        } else if (buttonText.equals("=")) {
            try {
                double result = evaluateExpression(input.toString());
                resultTv.setText(String.valueOf(result));
            } catch (Exception e) {
                resultTv.setText("Error");
            }
        } else if (buttonText.equals("Quit")) {
            finish();
        } else {
            input.append(buttonText);
        }

        solutionTv.setText(input.toString());
    }

    private double evaluateExpression(String expression) {
        try {
            if (expression.contains("√")) {
                return evaluateSquareRoot(expression);
            } else if (expression.contains("!")) {
                return evaluateFactorial(expression);
            } else if (expression.contains("^")) {
                return evaluateExponentiation(expression);
            }
            return evaluate(expression);
        } catch (Exception e) {
            return 0;
        }
    }

    private double evaluateSquareRoot(String expression) {
        String numberPart = expression.replace("√", "").trim();
        if (!numberPart.isEmpty()) {
            return Math.sqrt(Double.parseDouble(numberPart));
        }
        return 0;
    }

    private double evaluateFactorial(String expression) {
        String numberPart = expression.replace("!", "").trim();
        if (!numberPart.isEmpty()) {
            int num = Integer.parseInt(numberPart);
            return factorial(num);
        }
        return 0;
    }

    private int factorial(int n) {
        if (n == 0 || n == 1) return 1;
        return n * factorial(n - 1);
    }

    private double evaluateExponentiation(String expression) {
        String[] parts = expression.split("\\^");
        if (parts.length == 2) {
            double base = Double.parseDouble(parts[0].trim());
            double exponent = Double.parseDouble(parts[1].trim());
            return Math.pow(base, exponent);
        }
        return 0;
    }

    private double evaluate(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(num.toString()));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (isOperator(c)) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            default: return 0;
        }
    }
}
