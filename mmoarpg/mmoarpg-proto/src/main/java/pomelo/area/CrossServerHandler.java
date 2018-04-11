// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: crossServerHandler.proto

package pomelo.area;

public final class CrossServerHandler {
  private CrossServerHandler() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface TreasureOpenPushOrBuilder extends
      // @@protoc_insertion_point(interface_extends:pomelo.area.TreasureOpenPush)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 s2c_code = 1;</code>
     */
    boolean hasS2CCode();
    /**
     * <code>required int32 s2c_code = 1;</code>
     */
    int getS2CCode();

    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    boolean hasS2CMsg();
    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    java.lang.String getS2CMsg();
    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    com.google.protobuf.ByteString
        getS2CMsgBytes();

    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    java.util.List<pomelo.Common.OpenTimeInfo> 
        getS2COpenListList();
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    pomelo.Common.OpenTimeInfo getS2COpenList(int index);
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    int getS2COpenListCount();
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    java.util.List<? extends pomelo.Common.OpenTimeInfoOrBuilder> 
        getS2COpenListOrBuilderList();
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    pomelo.Common.OpenTimeInfoOrBuilder getS2COpenListOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code pomelo.area.TreasureOpenPush}
   */
  public static final class TreasureOpenPush extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:pomelo.area.TreasureOpenPush)
      TreasureOpenPushOrBuilder {
    // Use TreasureOpenPush.newBuilder() to construct.
    private TreasureOpenPush(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private TreasureOpenPush(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final TreasureOpenPush defaultInstance;
    public static TreasureOpenPush getDefaultInstance() {
      return defaultInstance;
    }

    public TreasureOpenPush getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private TreasureOpenPush(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              s2CCode_ = input.readInt32();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              s2CMsg_ = bs;
              break;
            }
            case 26: {
              if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                s2COpenList_ = new java.util.ArrayList<pomelo.Common.OpenTimeInfo>();
                mutable_bitField0_ |= 0x00000004;
              }
              s2COpenList_.add(input.readMessage(pomelo.Common.OpenTimeInfo.PARSER, extensionRegistry));
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
          s2COpenList_ = java.util.Collections.unmodifiableList(s2COpenList_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return pomelo.area.CrossServerHandler.internal_static_pomelo_area_TreasureOpenPush_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return pomelo.area.CrossServerHandler.internal_static_pomelo_area_TreasureOpenPush_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              pomelo.area.CrossServerHandler.TreasureOpenPush.class, pomelo.area.CrossServerHandler.TreasureOpenPush.Builder.class);
    }

    public static com.google.protobuf.Parser<TreasureOpenPush> PARSER =
        new com.google.protobuf.AbstractParser<TreasureOpenPush>() {
      public TreasureOpenPush parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new TreasureOpenPush(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<TreasureOpenPush> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int S2C_CODE_FIELD_NUMBER = 1;
    private int s2CCode_;
    /**
     * <code>required int32 s2c_code = 1;</code>
     */
    public boolean hasS2CCode() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 s2c_code = 1;</code>
     */
    public int getS2CCode() {
      return s2CCode_;
    }

    public static final int S2C_MSG_FIELD_NUMBER = 2;
    private java.lang.Object s2CMsg_;
    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    public boolean hasS2CMsg() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    public java.lang.String getS2CMsg() {
      java.lang.Object ref = s2CMsg_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          s2CMsg_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string s2c_msg = 2;</code>
     */
    public com.google.protobuf.ByteString
        getS2CMsgBytes() {
      java.lang.Object ref = s2CMsg_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        s2CMsg_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int S2C_OPENLIST_FIELD_NUMBER = 3;
    private java.util.List<pomelo.Common.OpenTimeInfo> s2COpenList_;
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    public java.util.List<pomelo.Common.OpenTimeInfo> getS2COpenListList() {
      return s2COpenList_;
    }
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    public java.util.List<? extends pomelo.Common.OpenTimeInfoOrBuilder> 
        getS2COpenListOrBuilderList() {
      return s2COpenList_;
    }
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    public int getS2COpenListCount() {
      return s2COpenList_.size();
    }
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    public pomelo.Common.OpenTimeInfo getS2COpenList(int index) {
      return s2COpenList_.get(index);
    }
    /**
     * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
     */
    public pomelo.Common.OpenTimeInfoOrBuilder getS2COpenListOrBuilder(
        int index) {
      return s2COpenList_.get(index);
    }

    private void initFields() {
      s2CCode_ = 0;
      s2CMsg_ = "";
      s2COpenList_ = java.util.Collections.emptyList();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasS2CCode()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, s2CCode_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getS2CMsgBytes());
      }
      for (int i = 0; i < s2COpenList_.size(); i++) {
        output.writeMessage(3, s2COpenList_.get(i));
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, s2CCode_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getS2CMsgBytes());
      }
      for (int i = 0; i < s2COpenList_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, s2COpenList_.get(i));
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static pomelo.area.CrossServerHandler.TreasureOpenPush parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(pomelo.area.CrossServerHandler.TreasureOpenPush prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code pomelo.area.TreasureOpenPush}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:pomelo.area.TreasureOpenPush)
        pomelo.area.CrossServerHandler.TreasureOpenPushOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return pomelo.area.CrossServerHandler.internal_static_pomelo_area_TreasureOpenPush_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return pomelo.area.CrossServerHandler.internal_static_pomelo_area_TreasureOpenPush_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                pomelo.area.CrossServerHandler.TreasureOpenPush.class, pomelo.area.CrossServerHandler.TreasureOpenPush.Builder.class);
      }

      // Construct using pomelo.area.CrossServerHandler.TreasureOpenPush.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getS2COpenListFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        s2CCode_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        s2CMsg_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        if (s2COpenListBuilder_ == null) {
          s2COpenList_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
        } else {
          s2COpenListBuilder_.clear();
        }
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return pomelo.area.CrossServerHandler.internal_static_pomelo_area_TreasureOpenPush_descriptor;
      }

      public pomelo.area.CrossServerHandler.TreasureOpenPush getDefaultInstanceForType() {
        return pomelo.area.CrossServerHandler.TreasureOpenPush.getDefaultInstance();
      }

      public pomelo.area.CrossServerHandler.TreasureOpenPush build() {
        pomelo.area.CrossServerHandler.TreasureOpenPush result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public pomelo.area.CrossServerHandler.TreasureOpenPush buildPartial() {
        pomelo.area.CrossServerHandler.TreasureOpenPush result = new pomelo.area.CrossServerHandler.TreasureOpenPush(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.s2CCode_ = s2CCode_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.s2CMsg_ = s2CMsg_;
        if (s2COpenListBuilder_ == null) {
          if (((bitField0_ & 0x00000004) == 0x00000004)) {
            s2COpenList_ = java.util.Collections.unmodifiableList(s2COpenList_);
            bitField0_ = (bitField0_ & ~0x00000004);
          }
          result.s2COpenList_ = s2COpenList_;
        } else {
          result.s2COpenList_ = s2COpenListBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof pomelo.area.CrossServerHandler.TreasureOpenPush) {
          return mergeFrom((pomelo.area.CrossServerHandler.TreasureOpenPush)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(pomelo.area.CrossServerHandler.TreasureOpenPush other) {
        if (other == pomelo.area.CrossServerHandler.TreasureOpenPush.getDefaultInstance()) return this;
        if (other.hasS2CCode()) {
          setS2CCode(other.getS2CCode());
        }
        if (other.hasS2CMsg()) {
          bitField0_ |= 0x00000002;
          s2CMsg_ = other.s2CMsg_;
          onChanged();
        }
        if (s2COpenListBuilder_ == null) {
          if (!other.s2COpenList_.isEmpty()) {
            if (s2COpenList_.isEmpty()) {
              s2COpenList_ = other.s2COpenList_;
              bitField0_ = (bitField0_ & ~0x00000004);
            } else {
              ensureS2COpenListIsMutable();
              s2COpenList_.addAll(other.s2COpenList_);
            }
            onChanged();
          }
        } else {
          if (!other.s2COpenList_.isEmpty()) {
            if (s2COpenListBuilder_.isEmpty()) {
              s2COpenListBuilder_.dispose();
              s2COpenListBuilder_ = null;
              s2COpenList_ = other.s2COpenList_;
              bitField0_ = (bitField0_ & ~0x00000004);
              s2COpenListBuilder_ = 
                com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                   getS2COpenListFieldBuilder() : null;
            } else {
              s2COpenListBuilder_.addAllMessages(other.s2COpenList_);
            }
          }
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasS2CCode()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        pomelo.area.CrossServerHandler.TreasureOpenPush parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (pomelo.area.CrossServerHandler.TreasureOpenPush) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int s2CCode_ ;
      /**
       * <code>required int32 s2c_code = 1;</code>
       */
      public boolean hasS2CCode() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 s2c_code = 1;</code>
       */
      public int getS2CCode() {
        return s2CCode_;
      }
      /**
       * <code>required int32 s2c_code = 1;</code>
       */
      public Builder setS2CCode(int value) {
        bitField0_ |= 0x00000001;
        s2CCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 s2c_code = 1;</code>
       */
      public Builder clearS2CCode() {
        bitField0_ = (bitField0_ & ~0x00000001);
        s2CCode_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object s2CMsg_ = "";
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public boolean hasS2CMsg() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public java.lang.String getS2CMsg() {
        java.lang.Object ref = s2CMsg_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            s2CMsg_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public com.google.protobuf.ByteString
          getS2CMsgBytes() {
        java.lang.Object ref = s2CMsg_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          s2CMsg_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public Builder setS2CMsg(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        s2CMsg_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public Builder clearS2CMsg() {
        bitField0_ = (bitField0_ & ~0x00000002);
        s2CMsg_ = getDefaultInstance().getS2CMsg();
        onChanged();
        return this;
      }
      /**
       * <code>optional string s2c_msg = 2;</code>
       */
      public Builder setS2CMsgBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        s2CMsg_ = value;
        onChanged();
        return this;
      }

      private java.util.List<pomelo.Common.OpenTimeInfo> s2COpenList_ =
        java.util.Collections.emptyList();
      private void ensureS2COpenListIsMutable() {
        if (!((bitField0_ & 0x00000004) == 0x00000004)) {
          s2COpenList_ = new java.util.ArrayList<pomelo.Common.OpenTimeInfo>(s2COpenList_);
          bitField0_ |= 0x00000004;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilder<
          pomelo.Common.OpenTimeInfo, pomelo.Common.OpenTimeInfo.Builder, pomelo.Common.OpenTimeInfoOrBuilder> s2COpenListBuilder_;

      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public java.util.List<pomelo.Common.OpenTimeInfo> getS2COpenListList() {
        if (s2COpenListBuilder_ == null) {
          return java.util.Collections.unmodifiableList(s2COpenList_);
        } else {
          return s2COpenListBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public int getS2COpenListCount() {
        if (s2COpenListBuilder_ == null) {
          return s2COpenList_.size();
        } else {
          return s2COpenListBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public pomelo.Common.OpenTimeInfo getS2COpenList(int index) {
        if (s2COpenListBuilder_ == null) {
          return s2COpenList_.get(index);
        } else {
          return s2COpenListBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder setS2COpenList(
          int index, pomelo.Common.OpenTimeInfo value) {
        if (s2COpenListBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureS2COpenListIsMutable();
          s2COpenList_.set(index, value);
          onChanged();
        } else {
          s2COpenListBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder setS2COpenList(
          int index, pomelo.Common.OpenTimeInfo.Builder builderForValue) {
        if (s2COpenListBuilder_ == null) {
          ensureS2COpenListIsMutable();
          s2COpenList_.set(index, builderForValue.build());
          onChanged();
        } else {
          s2COpenListBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder addS2COpenList(pomelo.Common.OpenTimeInfo value) {
        if (s2COpenListBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureS2COpenListIsMutable();
          s2COpenList_.add(value);
          onChanged();
        } else {
          s2COpenListBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder addS2COpenList(
          int index, pomelo.Common.OpenTimeInfo value) {
        if (s2COpenListBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureS2COpenListIsMutable();
          s2COpenList_.add(index, value);
          onChanged();
        } else {
          s2COpenListBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder addS2COpenList(
          pomelo.Common.OpenTimeInfo.Builder builderForValue) {
        if (s2COpenListBuilder_ == null) {
          ensureS2COpenListIsMutable();
          s2COpenList_.add(builderForValue.build());
          onChanged();
        } else {
          s2COpenListBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder addS2COpenList(
          int index, pomelo.Common.OpenTimeInfo.Builder builderForValue) {
        if (s2COpenListBuilder_ == null) {
          ensureS2COpenListIsMutable();
          s2COpenList_.add(index, builderForValue.build());
          onChanged();
        } else {
          s2COpenListBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder addAllS2COpenList(
          java.lang.Iterable<? extends pomelo.Common.OpenTimeInfo> values) {
        if (s2COpenListBuilder_ == null) {
          ensureS2COpenListIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, s2COpenList_);
          onChanged();
        } else {
          s2COpenListBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder clearS2COpenList() {
        if (s2COpenListBuilder_ == null) {
          s2COpenList_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
          onChanged();
        } else {
          s2COpenListBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public Builder removeS2COpenList(int index) {
        if (s2COpenListBuilder_ == null) {
          ensureS2COpenListIsMutable();
          s2COpenList_.remove(index);
          onChanged();
        } else {
          s2COpenListBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public pomelo.Common.OpenTimeInfo.Builder getS2COpenListBuilder(
          int index) {
        return getS2COpenListFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public pomelo.Common.OpenTimeInfoOrBuilder getS2COpenListOrBuilder(
          int index) {
        if (s2COpenListBuilder_ == null) {
          return s2COpenList_.get(index);  } else {
          return s2COpenListBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public java.util.List<? extends pomelo.Common.OpenTimeInfoOrBuilder> 
           getS2COpenListOrBuilderList() {
        if (s2COpenListBuilder_ != null) {
          return s2COpenListBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(s2COpenList_);
        }
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public pomelo.Common.OpenTimeInfo.Builder addS2COpenListBuilder() {
        return getS2COpenListFieldBuilder().addBuilder(
            pomelo.Common.OpenTimeInfo.getDefaultInstance());
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public pomelo.Common.OpenTimeInfo.Builder addS2COpenListBuilder(
          int index) {
        return getS2COpenListFieldBuilder().addBuilder(
            index, pomelo.Common.OpenTimeInfo.getDefaultInstance());
      }
      /**
       * <code>repeated .pomelo.OpenTimeInfo s2c_openList = 3;</code>
       */
      public java.util.List<pomelo.Common.OpenTimeInfo.Builder> 
           getS2COpenListBuilderList() {
        return getS2COpenListFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilder<
          pomelo.Common.OpenTimeInfo, pomelo.Common.OpenTimeInfo.Builder, pomelo.Common.OpenTimeInfoOrBuilder> 
          getS2COpenListFieldBuilder() {
        if (s2COpenListBuilder_ == null) {
          s2COpenListBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<
              pomelo.Common.OpenTimeInfo, pomelo.Common.OpenTimeInfo.Builder, pomelo.Common.OpenTimeInfoOrBuilder>(
                  s2COpenList_,
                  ((bitField0_ & 0x00000004) == 0x00000004),
                  getParentForChildren(),
                  isClean());
          s2COpenList_ = null;
        }
        return s2COpenListBuilder_;
      }

      // @@protoc_insertion_point(builder_scope:pomelo.area.TreasureOpenPush)
    }

    static {
      defaultInstance = new TreasureOpenPush(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:pomelo.area.TreasureOpenPush)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_pomelo_area_TreasureOpenPush_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_pomelo_area_TreasureOpenPush_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\030crossServerHandler.proto\022\013pomelo.area\032" +
      "\014common.proto\"a\n\020TreasureOpenPush\022\020\n\010s2c" +
      "_code\030\001 \002(\005\022\017\n\007s2c_msg\030\002 \001(\t\022*\n\014s2c_open" +
      "List\030\003 \003(\0132\024.pomelo.OpenTimeInfo2\024\n\022cros" +
      "sServerHandler2R\n\017crossServerPush\022?\n\020tre" +
      "asureOpenPush\022\035.pomelo.area.TreasureOpen" +
      "Push\032\014.pomelo.Void"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          pomelo.Common.getDescriptor(),
        }, assigner);
    internal_static_pomelo_area_TreasureOpenPush_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_pomelo_area_TreasureOpenPush_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_pomelo_area_TreasureOpenPush_descriptor,
        new java.lang.String[] { "S2CCode", "S2CMsg", "S2COpenList", });
    pomelo.Common.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}