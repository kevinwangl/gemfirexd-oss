/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */

/*
 * Changes for GemFireXD distributed data platform.
 *
 * Portions Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
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

#ifndef GFXD_STRUCT_PDXNODE_H
#define GFXD_STRUCT_PDXNODE_H


#include "gfxd_types.h"

#include "gfxd_struct_FieldDescriptor.h"
#include "gfxd_struct_Decimal.h"
#include "gfxd_struct_Timestamp.h"
#include "gfxd_struct_FieldValue.h"

namespace com { namespace pivotal { namespace gemfirexd { namespace thrift {

typedef struct _PDXNode__isset {
  _PDXNode__isset() : singleField(false), fields(false), refId(false) {}
  bool singleField;
  bool fields;
  bool refId;
} _PDXNode__isset;

class PDXNode {
 public:

  static const char* ascii_fingerprint; // = "5BAE4780BBF340F68755ECE4E12FB8B2";
  static const uint8_t binary_fingerprint[16]; // = {0x5B,0xAE,0x47,0x80,0xBB,0xF3,0x40,0xF6,0x87,0x55,0xEC,0xE4,0xE1,0x2F,0xB8,0xB2};

  PDXNode() : refId(0) {
  }

#if __cplusplus >= 201103L
  PDXNode(const PDXNode& other) = default;
  PDXNode& operator=(const PDXNode& other) = default;

  PDXNode(PDXNode&& other) :
      singleField(std::move(other.singleField)), fields(std::move(other.fields)),
      refId(other.refId), __isset(other.__isset) {
  }

  void assign(PDXNode&& other) {
    singleField.operator =(std::move(other.singleField));
    fields.operator =(std::move(other.fields));
    refId = other.refId;
    __isset = other.__isset;
  }

  PDXNode& operator=(PDXNode&& other) {
    assign(std::move(other));
    return *this;
  }
#endif
 
  virtual ~PDXNode() throw() {}

  FieldValue singleField;
  std::vector<FieldValue>  fields;
  int32_t refId;

  _PDXNode__isset __isset;

  void __set_singleField(const FieldValue& val) {
    singleField = val;
    __isset.singleField = true;
  }

  void __set_fields(const std::vector<FieldValue> & val) {
    fields = val;
    __isset.fields = true;
  }

  void __set_refId(const int32_t val) {
    refId = val;
    __isset.refId = true;
  }

  bool operator == (const PDXNode & rhs) const
  {
    if (__isset.singleField != rhs.__isset.singleField)
      return false;
    else if (__isset.singleField && !(singleField == rhs.singleField))
      return false;
    if (__isset.fields != rhs.__isset.fields)
      return false;
    else if (__isset.fields && !(fields == rhs.fields))
      return false;
    if (__isset.refId != rhs.__isset.refId)
      return false;
    else if (__isset.refId && !(refId == rhs.refId))
      return false;
    return true;
  }
  bool operator != (const PDXNode &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const PDXNode & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(PDXNode &a, PDXNode &b);

}}}} // namespace

#endif