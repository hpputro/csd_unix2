import os, random
from shutil import copyfile
from refactoring_methods import *

N_METHODS = 14 
def return_function_code(code, method_names):
    final_codes = []
    final_names = []

    Class_list, raw_code = extract_class(code)

    for class_name in Class_list:
        function_list, class_name = extract_function(class_name)

    for fun_code in function_list:

        for method_name in method_names:
            method_name_tem = method_name.replace('|', '')
            if method_name_tem.upper() in fun_code.split('\n')[0].upper():

                final_codes.append(fun_code)
                final_names.append(method_name)

    return final_codes, final_names


def generate_adversarial(k, code, method_names):

        method_name = method_names[0]
        function_list = []
        class_name = ''

        Class_list, raw_code = extract_class(code)

        for class_name in Class_list:
            function_list, class_name = extract_function(class_name)

        refac = []
        new_refactored_code = ''

        for code in function_list:
            if method_name not in code.split('\n')[0]:
                continue

            new_rf = code
            new_refactored_code = code

            # print(code)
            for t in range(k):
                refactors_list = [add_argumemts,    #a
                              add_local_variable,   #b
                              add_print,            #c
                              apply_plus_zero_math, #d
                              enhance_filed,        #e
                              enhance_for_loop,     #f
                              rename_api,           #g
                              rename_argument,      #h
                              rename_local_variable,#i
                              rename_method_name,   #j
                              return_optimal,       #k
                              dead_branch_while,    #l
                              dead_branch_for,      #m
                              duplication           #n
                              ]
                vv = 0

                while new_rf == new_refactored_code and vv <= N_METHODS:
                    try:
                        vv += 1

                        refactor       = random.choice(refactors_list)
                        print('*'*50 , refactor , '*'*50)
                        new_refactored_code = refactor(new_refactored_code)

                    except Exception as error:
                        print('error:\t', error)

                new_rf = new_refactored_code

                print('----------------------------OUT of WHILE----------------------------------', vv)
                print('----------------------------CHANGED THJIS TIME:----------------------------------', vv)

            refac.append(new_refactored_code)

        code_body = raw_code.strip() + ' ' + class_name.strip()
        for i in range(len(refac)):
            final_refactor = code_body.replace('vesal' + str(i), str(refac[i]))
            code_body = final_refactor


        return new_refactored_code


def generate_adversarial_json(k, code):
    final_refactor = ''
    function_list = []
    class_name = ''
    vv = 0
    if len(function_list) == 0:
        function_list.append(code)
    refac = []
    for code in function_list:


        new_rf = code
        new_refactored_code = code

        for t in range(k):

            refactors_list = [add_argumemts,        #a
                              add_local_variable,   #b
                              add_print,            #c
                              apply_plus_zero_math, #d
                              enhance_filed,        #e
                              enhance_for_loop,     #f
                              rename_api,           #g
                              rename_argument,      #h
                              rename_local_variable,#i
                              rename_method_name,   #j
                              return_optimal,       #k
                              dead_branch_while,    #l
                              dead_branch_for,      #m
                              duplication           #n
                              ]
            vv = 0

            while new_rf == new_refactored_code and vv <= N_METHODS:
                try:
                    vv += 1
                    refactor = random.choice(refactors_list)
                    print('*' * 50, refactor, '*' * 50)
                    new_refactored_code = refactor(new_refactored_code)

                except Exception as error:
                    print('error:\t', error)

            new_rf = new_refactored_code

        refac.append(new_refactored_code)

    print("refactoring finished")
    return refac


def generate_adversarial_file_level(k, code):
        new_refactored_code = ''
        new_rf = code
        new_refactored_code = code

        refactors_list = [add_argumemts,
                          add_local_variable,
                          add_print,
                          apply_plus_zero_math,
                          enhance_filed,
                          enhance_for_loop,
                          rename_api,
                          rename_argument,
                          rename_local_variable,
                          rename_method_name,
                          return_optimal,
                          dead_branch_while,
                          dead_branch_for,
                          duplication
                          ]

        applied = []

        for t in range(k):
            shuffled = refactors_list.copy()
            random.shuffle(shuffled)

            for refactor in shuffled:
                try:
                    result = refactor(new_refactored_code)
                    if result != new_refactored_code:
                        new_refactored_code = result
                        applied.append(refactor.__name__)
                        break
                except Exception as error:
                    result = ''

        return new_refactored_code, applied


if __name__ == '__main__':
    K = 1
    filename = '**.py'
    open_file = open(filename, 'r', encoding='ISO-8859-1')
    code = open_file.read()
    new_code = generate_adversarial_file_level(K, code)
    print(new_code)







