# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: person.proto

require 'google/protobuf'

Google::Protobuf::DescriptorPool.generated_pool.build do
  add_file("person.proto", :syntax => :proto3) do
    add_message "Person" do
      optional :name, :string, 1
      optional :age, :int32, 2
    end
  end
end

Person = ::Google::Protobuf::DescriptorPool.generated_pool.lookup("Person").msgclass
