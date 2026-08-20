import React from "react";
import DefaultItemSkeleton from "./template/default_item_skeleton";
import DefaultCardSkeleton from "./template/default_card_item_skeleton";
import SettingCardSkeleton from "./template/setting_card_item_skeleton";
import ValidationCardSkeleton from "./template/validation_card_item_skeleton";


interface MySkeletonProps {
  template: "default_item" | "default_card" | "setting_card" | "validation_card";
  count ?: number;
  containerClass ?: string | null;
  childenClass ?: string | null;
  skeletonItemClass ?: string | null;
}

const MySkeleton: React.FC<MySkeletonProps> = ({ template, count = 1, containerClass = '', childenClass = '', skeletonItemClass = '' }) => {
  const renderSkeleton = () => {
    switch (template) {
      case "default_item":
        return <DefaultItemSkeleton />;
      case "default_card":
        return <DefaultCardSkeleton />;
          case "setting_card":
        return <SettingCardSkeleton />;
         case "validation_card":
        return <ValidationCardSkeleton skeletonItemClass={skeletonItemClass} />;
      default:
        return null;
    }
  };

  return (
    <div className={`${containerClass}`}>
      {Array.from({ length: count }).map((_, index) => (
        <div className={`${childenClass}`} key={index}>{renderSkeleton()}</div>
      ))}
    </div>
  );
};

export default MySkeleton;
