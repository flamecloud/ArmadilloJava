/*******************************************************************************
 * Copyright 2013-2014 Sebastian Niemann <niemann@sra.uni-hannover.de>.
 * 
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Developers:
 *   Sebastian Niemann - Lead developer
 *   Daniel Kiechle - Unit testing
 ******************************************************************************/
#include <Expected.hpp>
using armadilloJava::Expected;

#include <iostream>
using std::cout;
using std::endl;

#include <utility>
using std::pair;

#include <armadillo>
using arma::Col;
using arma::randi;
using arma::distr_param;

#include <InputClass.hpp>
using armadilloJava::InputClass;

#include <Input.hpp>
using armadilloJava::Input;

namespace armadilloJava {
  class ExpectedRandomNumElemsDistrParam : public Expected {
    public:
      ExpectedRandomNumElemsDistrParam() {
        cout << "Compute ExpectedRandomNumElemsDistrParam(): " << endl;

        vector<vector<pair<string, void*>>> inputs = Input::getTestParameters({InputClass::Random, InputClass::NumElems, InputClass::DistrParam});

        for (vector<pair<string, void*>> input : inputs) {
          _fileSuffix = "";

          int n = 0;
          for (pair<string, void*> value : input) {
            switch (n) {
              case 0:
                _fileSuffix += value.first;
                _random = *static_cast<int*>(value.second);
                break;
              case 1:
                _fileSuffix += "," + value.first;
                _numElems = *static_cast<int*>(value.second);
                break;
              case 2:
                _fileSuffix += "," + value.first;
                _distrParam = *static_cast<distr_param*>(value.second);
                break;
            }
            ++n;
          }

          cout << "Using input: " << _fileSuffix << endl;

          expectedArmaRandi();
        }

        cout << "done." << endl;
      }

    protected:
      int _random;
      int _numElems;
      distr_param _distrParam;

      void expectedArmaRandi() {
        cout << "- Compute expectedArmaRandi() ... ";

        Mat<double> result = randi<Col<double>>(_numElems, _distrParam);
        for(int n = 2; n <= _random; n++) {
          result = (result * n + randi<Col<double>>(_numElems, _distrParam)) / (n + 1);
        }
        save<double>("Arma.randi", result);

        cout << "done." << endl;
      }

  };
}
