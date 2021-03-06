/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
/*

UNIT testing framework for C++.

By including fw_helper.hpp 3 macros are defined for use in writing
modular unit tests.

BEGIN_TEST(x) where x is the name of the test case.
  Use this to mark the beginning of a test case. When one test case
  fails through an ASSERT, testing continues with the next case.

END_TEST(x) where x is the name of the test case.
  Use this to mark the end of a test case.

The use of BEGIN_TEST and END_TEST actually define and create an instance of
a derivation of TestOp. When TestOps are created they are added to a list
of operations to be executed. The code between BEGIN_TEST and END_TEST becomes
the body of a void method that is invoked when the TestOp is executed.

ASSERT(cond,message) where cond is a conditional expression,
  and message is the failure message. If cond does not evaluate to a notion
  of true, then an TestException is created and thrown. The executor of
  TestOps will catch these exceptions, print them, including the file and
  line information from where the ASSERT was called. It will then move on
  to the next TestOp.

For Example... A test file may look like:
--------------------------------------------------------------------

// Include product classes prior to fw_helper.hpp

#include "fw_helper.hpp"

#include <iostream>

BEGIN_TEST(TestSomeCode)
  bool flag = false;
  ASSERT( flag == true, "This one will throw an exception." );
END_TEST(TestSomeCode)

BEGIN_TEST(AnotherTest)
  std::cout << "this test will get run, regardless of TestSomeCode failing." << std::endl;
END_TEST(AnotherTest)
---------------------------------------------------------------------

fw_helper.hpp contains the main for you, all you have to do is declare some
test behaviors, and they'll be handled run.

fwtest_Name is defined for you, as a (const char *) with the value given to BEGIN_TEST.

*/

#ifdef WIN32
// Must include WinSock2 so winsock.h doesn't get included.
#if WINVER == 0x0500
#undef _WINSOCKAPI_
#include <WinSock2.h>
#endif
#endif

#include <ace/ACE.h>
#include <ace/OS.h>

#include <typeinfo>

#include "fw_SignalHandler.hpp"

#include <list>
#include <string>

#include <stdio.h>
#include <sqlext.h>

#include "fw_TimeBomb.hpp"

#define ASSERT(x,y) if (!(x)) throw TestException(y,__LINE__,__FILE__)
#define FAIL(y) throw TestException(y,__LINE__,__FILE__)
#define LOG(y) dunit::log(y,__LINE__,__FILE__, 0)
#define SLEEP(x) dunit::sleep(x)
#define LOGSTR(y) dunit::log(y)

#if defined(WIN32) || defined(WIN64)
#define LOGF(fmt, ...) {\
  char logbuff[1024];\
  sprintf(logbuff,fmt, __VA_ARGS__); \
  LOG(logbuff); \
}
#else
#define LOGF(format, args...)  {\
      char logbuff[1024];\
      sprintf(logbuff,format, ##args);\
      LOG(logbuff);\
}
#endif

namespace dunit {
void log( std::string s, int lineno, const char* filename, int id );
void log( std::string s);
void sleep( int millis );
void setupCRTOutput( ); // disable windows popups.
};

class TestException {
  public:
  TestException( const char* msg, int lineno, const char* filename )
  : m_message( (char *)msg ), m_lineno( lineno ), m_filename( (char *)filename )
  { }

  void print( )
  {
    fprintf(stdout, "--->TestException: %s in %s at line %d<---\n",
        m_message.c_str(), m_filename, m_lineno);
  }
  std::string m_message;
  int m_lineno;
  char* m_filename;
};

// std::list holding names of all tests that failed.
std::list<std::string> failed;

class TestOp {
  public:
  TestOp( ) { }

  virtual ~TestOp( ) { }
  virtual void setup( )
  {
    fprintf( stdout, "## running test - %s ##\n", m_name.c_str() );
  }
  virtual void doTest( ) { fprintf( stdout, "do something useful.\n" ); }
  void run( )
  {
    try
    {
      this->setup();
      this->doTest();
    }
    catch ( TestException e )
    {
      e.print();
      failed.push_back( m_name );
    }
    catch ( /*gemfire::Exception ge*/... )
    {
      //ge.printStackTrace();
      failed.push_back( m_name );
    }
  }
  virtual std::string typeName( ) { return std::string( typeid( *this ).name() ); }
  virtual void init( );
  std::string m_name;
};

class SuiteMember
{
  public:
  SuiteMember( TestOp* test ) : m_test(test) { }
  TestOp* m_test;
};

// std::list holding all registered TestOp instances.
std::list<SuiteMember> tests;

void TestOp::init( ) {
  m_name = this->typeName();
  tests.push_back( SuiteMember(this) );
  fprintf( stdout, "Queued test[%s].\n", m_name.c_str() );
}

// main - suite trigger

extern "C" {

#ifdef WIN32
int wmain( )
#else
int main(int argc, char *argv[])
#endif
{
  dunit::setupCRTOutput();
  //SignalHandler::installBacktraceHandler( );
  // TimeBomb* tb = new TimeBomb();
  // tb->arm();
  try {
    int testsRun = 0;
    int failures = 0;
    fprintf( stdout, "Beginning test Suite.\n" );
    while( ! tests.empty() ) {
      SuiteMember aTest = tests.front();
      aTest.m_test->run();
      tests.pop_front();
      testsRun++;
    }
    while( ! failed.empty() ) {
      std::string name = failed.front();
      fprintf( stdout, "test named \"%s\" failed.\n", name.c_str() );
      failures++;
      failed.pop_front();
    }
    if ( failures != 0 ) {
      fprintf( stdout, "%d tests failed.\n", failures );
    }
    fprintf( stdout, "%d tests passed.\n", ( testsRun - failures ) );
    return failures;
  } catch ( ... ) {
    printf( "Exception: unhandled/unidentified exception reached main.\n" );
    fflush( stdout );
  }
  return 1;
}

}

#include "fw_no_cout.hpp"

#define BEGIN_TEST(x) class Test_##x : virtual public TestOp { public: Test_##x( ) { init(); } public: virtual void doTest( ) { const char* fwtest_Name = #x; \
LOG("Started test "#x);\
SQLHENV henv = SQL_NULL_HANDLE; \
SQLHDBC hdbc = SQL_NULL_HANDLE; \
SQLHSTMT hstmt = SQL_NULL_HANDLE; \
SQLHDESC hdesc = SQL_NULL_HANDLE; \
SQLRETURN retcode = SQL_SUCCESS; \

#define END_TEST(x) \
LOG("Completed test "#x"\n\n");\
} } a_##x;

#define GFXDDSN "gfxddsn"
#define GFXDSERVER "127.0.0.1"
#define GFXDSERVERPORT 1527
