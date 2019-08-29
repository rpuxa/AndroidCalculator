//
// Created by Григорий on 24.08.2019.
//
#include <jni.h>
#include "../../../../../../../Programs/SDK/ndk-bundle/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/stdlib.h"


enum errors {
    UNKNOWN_OPERATION = 100
};

enum instructions {
    PLUS = 0,
    MINUS = 1,
    TIMES = 2,
    DIVIDE = 3,
    SUM = 4,
    PRODUCT = 5,
    UNARY_MINUS = 6
};

enum headers {
    HEADER_EMPTY,
    HEADER_NUMBER,
    HEADER_CONSTANT,
    HEADER_INSTRUCTION,
    HEADER_VARIABLE,
    HEADER_ABSTRACT_INSTRUCTION,
    HEADER_ABSTRACT_VARIABLE,
    HEADER_CONDITION_DIVIDER,
    HEADER_CONVERSION_DIVIDER
};

struct instruction {
    unsigned int arity;
    unsigned int id;
};

union value {
    long long int toLong;
    double toDouble;
    struct instruction toInstruction;
    unsigned char bytes[8];
};

#pragma pack(push, 1)
struct token {
    unsigned char header;
    union value value;
};
#pragma pack(pop)
/*
struct stack {
    unsigned int size;
    unsigned int maxSize;
    struct token *first;
};

struct stack *new_stack() {
    struct stack *stack = malloc(sizeof(struct stack));
    stack->size = 0;
    stack->maxSize = 4;
    stack->first = malloc(stack->maxSize * sizeof(struct token));

    return stack;
}

void delete_stack(struct stack *stack) {
    free(stack->first);
    free(stack);
}

void push(struct stack *stack, struct token element) {
    if (stack->size == stack->maxSize) {
        unsigned int newSize = (stack->size * 3) >> 1;
        stack->first = realloc(stack->first, newSize);
        stack->maxSize = newSize;
    }
    unsigned int size = stack->size;
    *(stack->first + size) = element;
    stack->size = size + 1;
}

struct token pop(struct stack *stack) {
    unsigned int size = stack->size - 1;
    struct token token = *(stack->first + size);
    stack->size = size;
    return token;
}*/

void next(struct token **tokens) {
    do {
        *tokens = *tokens + 1;
    } while ((*tokens)->header == HEADER_EMPTY);
}

void previous(struct token **tokens) {
    do {
        *tokens = *tokens - 1;
    } while ((*tokens)->header == HEADER_EMPTY);
}

char get_args(int count, double *args, struct token **tokens) {
    int lastIndex = count - 1;
    for (int i = 0; i < count; ++i) {
        previous(tokens);
        if ((*tokens)->header == HEADER_NUMBER) {
            (*tokens)->header = HEADER_EMPTY;
            args[lastIndex - i] = (*tokens)->value.toDouble;
        } else {
            return 0;
        }
    }
    return 1;
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wuninitialized"

char run_instruction_if_available(struct token *tokens) {
    int instructionId = tokens->value.toInstruction.id;
    int arity = tokens->value.toInstruction.arity;
    double args[arity];
    double first, second, value;

    if (!get_args(arity, args, &tokens)) return 0;

    first = args[0];
    if (arity > 1)
        second = args[1];

    switch (instructionId) {
        case PLUS:
            value = first + second;
            break;

        case MINUS:
            value = first - second;
            break;

        case TIMES:
            value = first * second;
            break;

        case DIVIDE:
            value = first / second;
            break;

        default:
            exit(UNKNOWN_OPERATION);
    }
    tokens->header = HEADER_NUMBER;
    tokens->value.toDouble = value;

    return 1;
}

#pragma clang diagnostic pop

void calculate_if_available(struct token *tokens, int size) {
    for (int i = 0; i < size; i++) {
        struct token *token = tokens + i;
        switch (token->header) {
            case HEADER_INSTRUCTION:
                if (run_instruction_if_available(token))
                    token->header = HEADER_EMPTY;
                break;

            default:
                break;
        }
    }

}

void reduce(struct token *expression, int *size) {
    int i = 0;
    for (; expression[i].header != HEADER_EMPTY; ++i);

    int s = *size;
    for (int j = i + 1; j < s; ++j) {
        if (expression[j].header != HEADER_EMPTY) {
            expression[i++] = expression[j];
        }
    }

    *size = i;
}


JNIEXPORT jint JNICALL Java_ru_rpuxa_androidcalculator_calc_stages_NativeCalculator_calculate(
        JNIEnv
        *env,
        jclass type,
        jbyteArray
        expression_,
        jbyteArray conversions_
) {

    jbyte *expressionBytes = (*env)->GetByteArrayElements(env, expression_, NULL);
    int expressionSize = (*env)->GetArrayLength(env, expression_) / 9;
    jbyte *conversions = (*env)->GetByteArrayElements(env, conversions_, NULL);

    struct token *expression = (struct token *) expressionBytes;

    reduce(expression, &expressionSize);
    calculate_if_available(expression, expressionSize);
    reduce(expression, &expressionSize);


    (*env)->ReleaseByteArrayElements(env, expression_, expressionBytes, 0);

    return expressionSize;
}