
const TrustBadges = () => (
  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
    <div className="flex items-center gap-3 p-4 bg-blue-50 rounded-xl text-blue-800">
      <span className="text-2xl">🛡️</span>
      <div><p className="text-xs font-black uppercase">Authenticity</p><p className="text-[10px]">100% Genuine Product</p></div>
    </div>
    <div className="flex items-center gap-3 p-4 bg-orange-50 rounded-xl text-orange-800">
      <span className="text-2xl">✈️</span>
      <div><p className="text-xs font-black uppercase">Worldwide</p><p className="text-[10px]">Express Shipping</p></div>
    </div>
  </div>
);

export default TrustBadges;