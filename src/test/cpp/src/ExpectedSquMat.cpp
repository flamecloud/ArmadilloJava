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
using arma::Mat;
using arma::det;
using arma::log_det;
using arma::trace;
using arma::diagmat;
using arma::symmatu;
using arma::symmatl;
using arma::trimatu;
using arma::trimatl;

#include <InputClass.hpp>
using armadilloJava::InputClass;

#include <Input.hpp>
using armadilloJava::Input;

namespace armadilloJava {
  class ExpectedSquMat : public Expected {
    public:
      ExpectedSquMat() {
        cout << "Compute ExpectedSquMat(): " << endl;

          vector<vector<pair<string, void*>>> inputs = Input::getTestParameters({
            InputClass::SquMat
          });

          for (vector<pair<string, void*>> input : inputs) {
            _fileSuffix = "";

            int n = 0;
            for (pair<string, void*> value : input) {
              switch (n) {
                case 0:
                  _fileSuffix += value.first;
                  _squMat = *static_cast<Mat<double>*>(value.second);
                  break;
              }
              ++n;
            }

            cout << "Using input: " << _fileSuffix << endl;

            expectedArmaDet();
            expectedArmaLog_det();
            expectedArmaTrace();
            expectedArmaDiagmat();
            expectedArmaSymmatu();
            expectedArmaSymmatl();
            expectedArmaTrimatu();
            expectedArmaTrimatl();
          }

          cout << "done." << endl;
        }

    protected:
      Mat<double> _squMat;

      void expectedArmaDet() {
        cout << "- Compute expectedArmaDet() ... ";
        save<double>("Arma.det", Mat<double>({det(_squMat)}));
        cout << "done." << endl;
      }

      void expectedArmaLog_det() {
        cout << "- Compute expectedArmaLog_det() ... ";

        double val, sign;

        log_det(val, sign, _squMat);

        save<double>("Arma.log_detVal", Mat<double>({val}));
        save<double>("Arma.log_detSign", Mat<double>({sign}));

        cout << "done." << endl;
      }

      void expectedArmaTrace() {
        cout << "- Compute expectedArmaTrace() ... ";
        save<double>("Arma.trace", Mat<double>({trace(_squMat)}));
        cout << "done." << endl;
      }

      void expectedArmaDiagmat() {
        cout << "- Compute expectedArmaDiagmat() ... ";
        save<double>("Arma.diagmat", diagmat(_squMat));
        cout << "done." << endl;
      }

      void expectedArmaSymmatu() {
        cout << "- Compute expectedArmaSymmatu() ... ";
        save<double>("Arma.symmatu", symmatu(_squMat));
        cout << "done." << endl;
      }

      void expectedArmaSymmatl() {
        cout << "- Compute expectedArmaSymmatl() ... ";
        save<double>("Arma.symmatl", symmatl(_squMat));
        cout << "done." << endl;
      }

      void expectedArmaTrimatu() {
        cout << "- Compute expectedArmaTrimatu() ... ";
        save<double>("Arma.trimatu", trimatu(_squMat));
        cout << "done." << endl;
      }

      void expectedArmaTrimatl() {
        cout << "- Compute expectedArmaTrimatl() ... ";
        save<double>("Arma.trimatl", trimatl(_squMat));
        cout << "done." << endl;
      }
  };
}
